package com.momo.savanger.integration.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.category.Category;
import com.momo.savanger.api.category.CategoryQuery;
import com.momo.savanger.api.category.CategoryRepository;
import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.category.CreateCategoryDto;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/category-it-data.sql")
@Sql(value = "classpath:/sql/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class CategoryServiceIt {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void testCreate_validPayload_shouldSaveTag() {
        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setCategoryName("Smetki");
        categoryDto.setBudgetId(1001L);

        this.categoryService.create(categoryDto);

        List<Category> categories = this.categoryRepository.findAll();

        assertThat(List.of("Home", "Smetki", "Dom"))
                .hasSameElementsAs(
                        categories.stream().map(Category::getCategoryName).toList()
                );
    }

    @Test
    public void testCreate_emptyPayload_shouldThrowException() {
        CreateCategoryDto categoryDto = new CreateCategoryDto();

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.categoryService.create(categoryDto);
        });
    }

    @Test
    public void testFindById_validId_shouldReturnBudget() {
        Category category = this.categoryService.findById(1001L);

        assertNotNull(category);
        assertEquals("Home", category.getCategoryName());
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        assertThrows(ApiException.class, () -> {
            this.categoryService.findById(544L);
        });
    }

    @Test
    public void testSearchCategories_valid_shouldReturnCategories() {
        CreateCategoryDto dto = new CreateCategoryDto();
        dto.setCategoryName("Smetki");
        dto.setBudgetId(1001L);
        dto.setBudgetCap(BigDecimal.valueOf(100));

        Category category = this.categoryService.create(dto);

        CategoryQuery query = new CategoryQuery();

        PageQuery pageQuery = new PageQuery(0, 3);

        SortQuery sortQuery = new SortQuery("ds", SortDirection.ASC);

        // Search by budgetId
        query.setSort(sortQuery);
        query.setPage(pageQuery);
        query.setBudgetId(1001L);

        Page<Category> categories = this.categoryService.searchCategories(query);

        assertEquals(2, categories.getTotalElements());

        assertEquals(category.getId(), categories.getContent().getFirst().getId());
        assertEquals(1001L, categories.getContent().getLast().getId());

        //Test by name

        query.setBudgetId(1001L);
        query.setCategoryName("Smetki");

        categories = this.categoryService.searchCategories(query);

        assertEquals(1, categories.getTotalElements());

        assertEquals("Smetki", categories.getContent().getFirst().getCategoryName());

        // Test by budget cap

        query.setCategoryName(null);

        BetweenQuery<BigDecimal> budgetCap = new BetweenQuery<>(BigDecimal.valueOf(0),
                BigDecimal.valueOf(500));

        query.setBudgetCap(budgetCap);
        categories = this.categoryService.searchCategories(query);

        assertEquals(2, categories.getTotalElements());

        assertEquals("Smetki", categories.getContent().getFirst().getCategoryName());
        assertEquals("Home", categories.getContent().getLast().getCategoryName());

        //Test budget cap 2
        budgetCap = new BetweenQuery<>(BigDecimal.valueOf(20), BigDecimal.valueOf(299));

        query.setBudgetCap(budgetCap);

        categories = this.categoryService.searchCategories(query);

        assertEquals(1, categories.getTotalElements());
        assertEquals("Smetki", categories.getContent().getFirst().getCategoryName());
    }
}

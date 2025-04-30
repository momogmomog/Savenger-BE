package com.momo.savanger.integration.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.category.Category;
import com.momo.savanger.api.category.CategoryRepository;
import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.category.CreateCategoryDto;
import com.momo.savanger.error.ApiException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
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

        assertThat(List.of("Home", "Smetki"))
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
}

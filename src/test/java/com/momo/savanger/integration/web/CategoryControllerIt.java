package com.momo.savanger.integration.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.category.Category;
import com.momo.savanger.api.category.CategoryRepository;
import com.momo.savanger.api.category.CreateCategoryDto;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/category-it-data.sql")
@Sql("classpath:/sql/budgets_participants-it-data.sql")
@Sql(value = "classpath:/sql/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class CategoryControllerIt extends BaseControllerIt {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayloadWithLoggedOwner_shouldSaveCategory() throws Exception {

        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setCategoryName("Test");
        categoryDto.setBudgetCap(BigDecimal.valueOf(323));
        categoryDto.setBudgetId(1001L);

        super.postOK(Endpoints.CATEGORIES, categoryDto);

        List<Category> categories = this.categoryRepository.findAll();

        assertThat(List.of("Home", "Dom", "Test"))
                .hasSameElementsAs(
                        categories.stream().map(Category::getCategoryName).toList()
                );
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_validPayloadWithLoggedParticipant_shouldSaveCategory() throws Exception {

        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setCategoryName("Test");
        categoryDto.setBudgetCap(BigDecimal.valueOf(323));
        categoryDto.setBudgetId(1001L);

        super.postOK(Endpoints.CATEGORIES, categoryDto);

        List<Category> categories = this.categoryRepository.findAll();

        assertThat(List.of("Home", "Dom", "Test"))
                .hasSameElementsAs(
                        categories.stream().map(Category::getCategoryName).toList()
                );
    }

    @Test
    public void testCreate_withAnonymousUser_shouldReturnException() throws Exception {

        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setCategoryName("Test");
        categoryDto.setBudgetCap(BigDecimal.valueOf(323));
        categoryDto.setBudgetId(1001L);

        super.post(Endpoints.CATEGORIES, categoryDto, HttpStatus.BAD_REQUEST);
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_EmptyPayload() throws Exception {
        CreateCategoryDto categoryDto = new CreateCategoryDto();

        super.post(
                Endpoints.CATEGORIES,
                categoryDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryName\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_InvalidPayload() throws Exception {
        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setCategoryName("");
        categoryDto.setBudgetCap(BigDecimal.valueOf(-2));
        categoryDto.setBudgetId(1001L);

        super.post(
                Endpoints.CATEGORIES,
                categoryDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetCap\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_zeroBudgetCap() throws Exception {
        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setCategoryName("");
        categoryDto.setBudgetId(1001L);
        categoryDto.setBudgetCap(BigDecimal.valueOf(0));

        super.post(
                Endpoints.CATEGORIES,
                categoryDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

    }
}

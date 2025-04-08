package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.budget.BudgetRepository;
import com.momo.savanger.api.budget.CreateBudgetDto;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetControllerIt extends BaseControllerIt {

    @Autowired
    private BudgetRepository budgetRepository;

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testSaveBudget_validPayload_shouldSaveBudget() throws Exception {

        CreateBudgetDto createBudgetDto = new CreateBudgetDto();
        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.now());
        createBudgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        createBudgetDto.setBalance(BigDecimal.valueOf(243.4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);


        super.postOK(Endpoints.BUDGETS, createBudgetDto);
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreateBudget_InvalidPayload() throws Exception {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();

        super.post(
                Endpoints.BUDGETS,
                createBudgetDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(7)),
                jsonPath("fieldErrors.[?(@.field == \"budgetName\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"dateStarted\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"dueDate\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"active\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"autoRevise\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"RRule\")]").exists()
        );

        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREuQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.now());
        createBudgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        createBudgetDto.setBalance(BigDecimal.valueOf(243.4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);

        super.post(
                Endpoints.BUDGETS,
                createBudgetDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"RRule\")]").exists()
        );

        createBudgetDto.setRecurringRule("");
        createBudgetDto.setBudgetName("");

        super.post(
                Endpoints.BUDGETS,
                createBudgetDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath("fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"RRule\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"Length\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"budgetName\" && @.constraintName == \"Length\")]").exists()
        );

        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
//        createBudgetDto.setDateStarted(LocalDateTime.parse("32-12-2012 00:00:00"));

        final var payloadMap = new HashMap<String, String>();
        payloadMap.put("dateStarted", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        super.post(
                Endpoints.BUDGETS,
                payloadMap,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.[?(@.field == \"dateStarted\" && @.constraintName == \"NotNull\")]").doesNotExist()

        );

        payloadMap.put("dateStarted", "el data");

        super.post(
                Endpoints.BUDGETS,
                payloadMap,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.[?(@.field == \"dateStarted\" && @.constraintName == \"NotNull\")]").exists()

        );
    }

}

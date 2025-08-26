package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionRepository;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@AutoConfigureTestDatabase
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/debt/budget-it-data.sql")
@Sql("classpath:/sql/debt/budgets_participants-it-data.sql")
@Sql("classpath:/sql/debt/tag-it-data.sql")
@Sql("classpath:/sql/debt/category-it-data.sql")
@Sql("classpath:/sql/debt/transaction-it-data.sql")
@Sql("classpath:/sql/debt/transactions_tags-it-data.sql")
@Sql("classpath:/sql/debt/revision-it-data.sql")
@Sql("classpath:/sql/debt/debt-it-data.sql")
@Sql("classpath:/sql/debt/prepayment-it-data.sql")
@Sql("classpath:/sql/debt/recurring_transaction-it-data.sql")
@Sql(value = "classpath:/sql/debt/del-recurring_transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-prepayment-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class RecurringTransactionControlletIt extends BaseControllerIt {

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreateRecurringTransaction() throws Exception {

        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();

        rTransactionDto.setAmount(BigDecimal.valueOf(0.05));
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setBudgetId(1002L);
        rTransactionDto.setAutoExecute(false);
        rTransactionDto.setType(TransactionType.EXPENSE);

        CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
        prepaymentDto.setAmount(BigDecimal.valueOf(1));
        prepaymentDto.setRecurringTransaction(rTransactionDto);
        prepaymentDto.setBudgetId(1002L);
        prepaymentDto.setName("kroki");
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(10));

        super.postOK(Endpoints.PREPAYMENTS, prepaymentDto);

        assertEquals(2, this.recurringTransactionRepository.findAll().size());

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload_shouldThrowException() throws Exception {

        //Test empty recurring transaction
        CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();

        CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
        prepaymentDto.setAmount(BigDecimal.valueOf(1));
        prepaymentDto.setRecurringTransaction(rTransactionDto);
        prepaymentDto.setBudgetId(1002L);
        prepaymentDto.setName("kroki");
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(10));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(6)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.type\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.autoExecute\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"RRule\")]").exists()
        );

        //Test recurringRule

        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setAutoExecute(false);

        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVA=1");

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"RRule\")]").exists()
        );

        //Test amount
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setAmount(BigDecimal.valueOf(-34));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

        //Test budgetID

        rTransactionDto.setAmount(BigDecimal.ZERO);
        rTransactionDto.setBudgetId(1003L);
        prepaymentDto.setBudgetId(1003L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()

        );

    }
}

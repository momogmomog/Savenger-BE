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
import java.util.List;
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
@Sql("classpath:/sql/prepayment/budget-it-data.sql")
@Sql("classpath:/sql/prepayment/budgets_participants-it-data.sql")
@Sql("classpath:/sql/prepayment/tag-it-data.sql")
@Sql("classpath:/sql/prepayment/category-it-data.sql")
@Sql("classpath:/sql/prepayment/transaction-it-data.sql")
@Sql("classpath:/sql/prepayment/transactions_tags-it-data.sql")
@Sql("classpath:/sql/prepayment/revision-it-data.sql")
@Sql("classpath:/sql/prepayment/debt-it-data.sql")
@Sql("classpath:/sql/prepayment/prepayment-it-data.sql")
@Sql("classpath:/sql/prepayment/recurring_transaction-it-data.sql")
@Sql(value = "classpath:/sql/prepayment/del-recurring_transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-prepayment-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class RecurringTransactionControllerIt extends BaseControllerIt {

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
                jsonPath("fieldErrors.length()", is(7)),
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
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"RRule\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDtoBudgetIds\")]").exists()

        );

        //Test recurringRule

        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setAutoExecute(false);

        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVA=1");

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(4)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"RRule\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDtoBudgetIds\")]").exists()

        );

        //Test amount
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setAmount(BigDecimal.valueOf(-34));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDtoBudgetIds\")]").exists()

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

        prepaymentDto.setBudgetId(1001L);
        rTransactionDto.setBudgetId(1001L);
        rTransactionDto.setCategoryId(1002L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.categoryId\" && @.constraintName == \"ValidRecurringTransactionDto\" "
                                + "&& @.message == \"Category does not exist or budget is not valid.\")]").exists()
        );

        rTransactionDto.setCategoryId(1001L);
        rTransactionDto.setDebtId(1001L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.debtId\" && @.constraintName == \"ValidRecurringTransactionDto\" "
                                + "&& @.message == \"Debt is not valid.\")]").exists()
        );

        rTransactionDto.setDebtId(101L);
        rTransactionDto.setTagIds(List.of(1001L, 1002L, 1003L));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.tagIds\" && @.constraintName == \"ValidRecurringTransactionDto\" "
                                + "&& @.message == \"Invalid tags: [1002, 1003]\")]").exists()
        );
    }
}

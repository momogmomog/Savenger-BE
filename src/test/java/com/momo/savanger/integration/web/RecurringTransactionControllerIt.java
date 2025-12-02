package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionRepository;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreateRecurringTransaction() throws Exception {

        CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();

        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setAmount(BigDecimal.valueOf(20));
        rTransactionDto.setBudgetId(1001L);
        rTransactionDto.setAutoExecute(false);

        final CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
        prepaymentDto.setAmount(BigDecimal.valueOf(100));
        prepaymentDto.setRecurringTransaction(rTransactionDto);
        prepaymentDto.setBudgetId(1001L);
        prepaymentDto.setName("kroki");
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(10));

        super.postOK(Endpoints.PREPAYMENTS, prepaymentDto);

        assertEquals(2, this.recurringTransactionRepository.findAll().size());

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload_shouldThrowException() throws Exception {

        //Test empty recurring transaction
        final CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();

        final CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
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
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDto\")]").exists()

        );

        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setRecurringRule("FRE=DAILY;INTERVAL=1");
        rTransactionDto.setAmount(BigDecimal.valueOf(-20));
        rTransactionDto.setBudgetId(101L);
        rTransactionDto.setAutoExecute(false);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(4)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"RRule\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDto\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_invalidUser() throws Exception {

        final CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
        prepaymentDto.setAmount(BigDecimal.valueOf(100));
        prepaymentDto.setRecurringTransactionId(1001L);
        prepaymentDto.setBudgetId(1001L);
        prepaymentDto.setName("kroki");
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(10));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && "
                                + "@.constraintName == \"CanAccessBudget\")]").exists()

        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testPay_validPayload_shouldPayTransaction() throws Exception {

        super.postOK("/recurring-transaction/1001/pay-r-transaction", null);

        assertEquals(2, this.transactionRepository.findAll().size());

    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testPay_invalidBudgetOwner_shouldThrowException() throws Exception {

        super.post("/recurring-transaction/1001/pay-r-transaction",
                null,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"rTransactionId\" && "
                                + "@.constraintName == \"ValidRecurringTransaction\" &&"
                                + "@.message == \"Access denied\")]").exists()

        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload() throws Exception {

        super.post("/recurring-transaction/1002/pay-r-transaction",
                null,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"rTransactionId\" && "
                                + "@.constraintName == \"ValidRecurringTransaction\" &&"
                                + "@.message == \"Recurring transaction with this id and budget id does not exist.\")]").exists()

        );

        super.postOK("/recurring-transaction/1001/pay-r-transaction", null);

        super.postOK("/recurring-transaction/1001/pay-r-transaction", null);

        super.post("/recurring-transaction/1001/pay-r-transaction",
                null,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"rTransactionId\" && "
                                + "@.constraintName == \"ValidRecurringTransaction\" &&"
                                + "@.message == \"Recurring transaction with this id and budget id does not exist.\")]").exists()

        );

    }
}

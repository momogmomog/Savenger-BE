package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.Transaction;
import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionRepository;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.constants.Endpoints;
import com.momo.savanger.web.RecurringTransactionController;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
    private RecurringTransactionService recurringTransactionService;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RecurringTransactionController recurringTransactionController;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreateRecurringTransaction() throws Exception {

        final CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();

        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setAmount(BigDecimal.valueOf(20));
        rTransactionDto.setBudgetId(1001L);
        rTransactionDto.setCategoryId(1001L);
        rTransactionDto.setIncludeInBalance(false);
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
        prepaymentDto.setAmount(BigDecimal.valueOf(1000));
        prepaymentDto.setRecurringTransaction(rTransactionDto);
        prepaymentDto.setBudgetId(1002L);
        prepaymentDto.setName("kroki");
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(10));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(9)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.type\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.autoExecute\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.categoryId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"IsBudgetBalanceBigger\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.includeInBalance\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDto\")]").exists()

        );

        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setRecurringRule("FRE=DAILY;INTERVAL=1");
        rTransactionDto.setAmount(BigDecimal.valueOf(-20));
        rTransactionDto.setBudgetId(101L);
        rTransactionDto.setIncludeInBalance(false);
        rTransactionDto.setAutoExecute(false);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(6)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.recurringRule\" && @.constraintName == \"RRule\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.amount\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.categoryId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"ValidPrepaymentDto\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"IsBudgetBalanceBigger\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransaction.budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_invalidUser() throws Exception {

        final CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
        prepaymentDto.setAmount(BigDecimal.valueOf(100));
        prepaymentDto.setRecurringTransactionId(9001L);
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

        super.postOK("/prepayments/pay/9001", null);

        assertEquals(2, this.transactionRepository.findAll().size());

        Transaction transaction = this.transactionRepository.findAll().stream()
                .filter(t -> t.getRecurringTransactionId() != null)
                .findFirst()
                .orElse(null);

        assertNotNull(transaction);
        assertEquals(9001L, transaction.getRecurringTransactionId());
        assertEquals(1001L, transaction.getPrepaymentId());

    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testPay_invalidBudgetOwner_shouldThrowException() throws Exception {

        super.post("/prepayments/pay/9001",
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

        super.post("/prepayments/pay/1002",
                null,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"rTransactionId\" && "
                                + "@.constraintName == \"ValidRecurringTransaction\" &&"
                                + "@.message == \"Recurring transaction with this id and budget id does not exist.\")]").exists()

        );

        // 1st payment
        super.postOK("/prepayments/pay/9001", null);

        // 2nd payment - remaining amount should be now 0
        super.postOK("/prepayments/pay/9001", null);

        // prepayment is already completed
        super.post("/prepayments/pay/9001",
                null,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"rTransactionId\" && "
                                + "@.constraintName == \"ValidRecurringTransaction\" &&"
                                + "@.message == \"Recurring transaction with this id and budget id does not exist.\")]").exists()

        );

    }

    @Test
    @Transactional
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void execute_validId() throws Exception {
        CreateRecurringTransactionDto createDto = new CreateRecurringTransactionDto();
        createDto.setBudgetId(1001L);
        createDto.setType(TransactionType.EXPENSE);
        createDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createDto.setCategoryId(1001L);
        createDto.setAutoExecute(false);
        createDto.setAmount(BigDecimal.valueOf(100));

        RecurringTransaction rTransactionWithoutPrepayment = this.recurringTransactionService.create(
                createDto
        );

        CreateTransactionDto overridesDto = new CreateTransactionDto();
        overridesDto.setBudgetId(1001L);
        overridesDto.setAmount(BigDecimal.TEN);
        overridesDto.setType(TransactionType.INCOME);
        overridesDto.setCategoryId(1001L);
        overridesDto.setTagIds(List.of(1001L));
        overridesDto.setDateCreated(LocalDateTime.now());

        //This returns rTransaction before override
        super.postOK(
                "/recurring-transactions/" + rTransactionWithoutPrepayment.getId() + "/execute",
                overridesDto
        );

        Transaction transaction = this.transactionRepository.findAll().stream()
                .filter(t -> t.getRecurringTransactionId() != null)
                .findFirst()
                .orElse(null);

        assertNotNull(transaction);
        assertEquals(
                rTransactionWithoutPrepayment.getId(),
                transaction.getRecurringTransactionId()
        );
        assertNull(transaction.getPrepaymentId());
        assertEquals(BigDecimal.TEN, transaction.getAmount());
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals(1, transaction.getTags().size());
        assertEquals(1001L, transaction.getTags().getFirst().getId());

    }

    @Test
    @Transactional
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void execute_withPrepaymentAttached_shouldApplyOverridesAndUpdatePrepayment()
            throws Exception {

        CreateTransactionDto overridesDto = new CreateTransactionDto();
        overridesDto.setBudgetId(1001L);
        overridesDto.setAmount(BigDecimal.TEN);
        overridesDto.setType(TransactionType.EXPENSE);
        overridesDto.setCategoryId(1001L);
        overridesDto.setTagIds(List.of(1001L));
        overridesDto.setComment("Executed prepayment override");
        overridesDto.setDateCreated(LocalDateTime.now());

        super.postOK("/recurring-transactions/9001/execute",
                overridesDto
        );

        Transaction transaction = this.transactionRepository.findAll().stream()
                .filter(t -> t.getRecurringTransactionId() != null)
                .findFirst()
                .orElse(null);

        assertNotNull(transaction);
        assertEquals(9001L, transaction.getRecurringTransactionId());
        assertEquals(1001L, transaction.getPrepaymentId());
        assertEquals(BigDecimal.TEN, transaction.getAmount());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals("Executed prepayment override", transaction.getComment());
        assertEquals(1, transaction.getTags().size());
        assertEquals(1001L, transaction.getTags().getFirst().getId());

        Prepayment prepayment = this.prepaymentService.findById(1001L);
        assertEquals(
                BigDecimal.valueOf(190).setScale(2, RoundingMode.HALF_DOWN),
                prepayment.getRemainingAmount().setScale(2, RoundingMode.HALF_DOWN)
        );
        assertFalse(prepayment.getCompleted());
    }

    @Test
    @Transactional
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void execute_invalidId() throws Exception {

        CreateTransactionDto overridesDto = new CreateTransactionDto();
        overridesDto.setBudgetId(1001L);
        overridesDto.setAmount(BigDecimal.TEN);
        overridesDto.setType(TransactionType.INCOME);
        overridesDto.setCategoryId(1001L);
        overridesDto.setTagIds(List.of(1001L));
        overridesDto.setDateCreated(LocalDateTime.now());

        //This returns rTransaction before override
        super.post("/recurring-transactions/10013/execute",
                overridesDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"rTransactionId\" && "
                                + "@.constraintName == \"ValidRecurringTransaction\" &&"
                                + "@.message == \"Recurring transaction with this id and budget id does not exist.\")]").exists()

        );
    }
}

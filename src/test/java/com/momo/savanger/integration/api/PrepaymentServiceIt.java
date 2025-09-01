package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentRepository;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionRepository;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

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
public class PrepaymentServiceIt {

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private PrepaymentRepository prepaymentRepository;

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    private RecurringTransactionService recurringTransactionService;

    @Test
    @Transactional
    public void testCreate_validPayload_shouldCreatePayment() {

        assertEquals(2, this.prepaymentRepository.findAll().size());

        CreateRecurringTransactionDto createRecurringTransactionDto = new CreateRecurringTransactionDto();
        createRecurringTransactionDto.setAmount(BigDecimal.valueOf(20));
        createRecurringTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createRecurringTransactionDto.setType(TransactionType.EXPENSE);
        createRecurringTransactionDto.setBudgetId(1001L);
        createRecurringTransactionDto.setCategoryId(1001L);
        createRecurringTransactionDto.setAutoExecute(false);

        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransaction(createRecurringTransactionDto);

        this.prepaymentService.create(createPrepaymentDto);

        assertEquals(3, this.prepaymentRepository.findAll().size());
    }

    @Test
    @Transactional
    public void testAddPrepaymentIdToRTransaction_validPayload_shouldCreatePayment() {
        assertEquals(1001L, this.recurringTransactionService.findById(1001L).getPrepaymentId());

        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransactionId(1001L);

        this.prepaymentService.create(createPrepaymentDto);

        assertEquals(1, this.recurringTransactionService.findById(1001L).getPrepaymentId());
    }

    @Test
    @Transactional
    public void testCreate_emptyPayload_shouldThrowException() {
        CreatePrepaymentDto dto = new CreatePrepaymentDto();

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.prepaymentService.create(dto);
        });

    }

    @Test
    @Transactional
    public void testCreate_validPayload_shouldCreateRTransaction() {
        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        CreateRecurringTransactionDto createRecurringTransactionDto = new CreateRecurringTransactionDto();
        createRecurringTransactionDto.setAmount(BigDecimal.valueOf(20));
        createRecurringTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createRecurringTransactionDto.setType(TransactionType.EXPENSE);
        createRecurringTransactionDto.setBudgetId(1001L);
        createRecurringTransactionDto.setCategoryId(1001L);
        createRecurringTransactionDto.setAutoExecute(false);

        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransaction(createRecurringTransactionDto);

        this.prepaymentService.create(createPrepaymentDto);

        assertEquals(2, this.recurringTransactionRepository.findAll().size());
    }

    @Test
    @Transactional
    public void testCreate_validPayload_shouldUseExistingRTransaction() {
        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransactionId(1001L);

        this.prepaymentService.create(createPrepaymentDto);

        assertEquals(1, this.recurringTransactionRepository.findAll().size());
    }

    @Test
    public void testFindById_validPayload_shouldFindPayment() {
        Prepayment prepayment = this.prepaymentService.findById(1001L);

        assertNotNull(prepayment);
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0015,
                () -> this.prepaymentService.findById(10040L));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_validPayload_shouldSumAmount() {
        BigDecimal sum = this.prepaymentService.getPrepaymentAmountSumByBudgetId(1001L);

        assertEquals(BigDecimal.valueOf(200.00), sum.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_withoutPrepaymentsBudget_shouldThrowException() {
        BigDecimal sum = this.prepaymentService.getPrepaymentAmountSumByBudgetId(1003L);

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_DOWN),
                sum.setScale(1, RoundingMode.HALF_DOWN));
    }

}

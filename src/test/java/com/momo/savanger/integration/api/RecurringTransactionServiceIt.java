package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionRepository;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class RecurringTransactionServiceIt {

    @Autowired
    private RecurringTransactionService rTransactionService;

    @Autowired
    private RecurringTransactionRepository rTransactionRepository;

    @Autowired
    private PrepaymentService prepaymentService;

    @Test
    @Transactional
    public void testCreate_prepaymentPayload_shouldCreateRTransaction()
            throws InvalidRecurrenceRuleException {
        assertEquals(1, this.rTransactionRepository.findAll().size());

        CreateRecurringTransactionDto transactionDto = new CreateRecurringTransactionDto();
        transactionDto.setBudgetId(1001L);
        transactionDto.setType(TransactionType.EXPENSE);
        transactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        transactionDto.setCategoryId(1001L);
        transactionDto.setAutoExecute(false);
        transactionDto.setAmount(BigDecimal.valueOf(0.80));

        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setName("test");
        createPrepaymentDto.setAmount(BigDecimal.valueOf(10));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(12));
        createPrepaymentDto.setRecurringTransaction(transactionDto);

        this.prepaymentService.create(createPrepaymentDto);

        assertEquals(2, this.rTransactionRepository.findAll().size());
    }

    @Test
    @Transactional
    public void testCreate_validPayload_shouldCreateRTransaction()
            throws InvalidRecurrenceRuleException {
        assertEquals(1, this.rTransactionRepository.findAll().size());

        CreateRecurringTransactionDto transactionDto = new CreateRecurringTransactionDto();
        transactionDto.setBudgetId(1001L);
        transactionDto.setType(TransactionType.EXPENSE);
        transactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        transactionDto.setCategoryId(1001L);
        transactionDto.setAutoExecute(false);
        transactionDto.setAmount(BigDecimal.valueOf(0.80));

        RecurringTransaction rTransaction = this.rTransactionService.create(transactionDto);

        assertEquals(2, this.rTransactionRepository.findAll().size());

        //Test data
        assertEquals(transactionDto.getBudgetId(),
                rTransaction.getId());
        assertEquals(transactionDto.getType(),
                rTransaction.getType());
        assertEquals(transactionDto.getRecurringRule(),
                rTransaction.getRecurringRule());
        assertEquals(transactionDto.getCategoryId(),
                rTransaction.getCategoryId());
        assertEquals(transactionDto.getAutoExecute(),
                rTransaction.getAutoExecute());
        assertEquals(transactionDto.getAmount(),
                rTransaction.getAmount());
    }

    @Test
    @Transactional
    public void testCreate_emptyPayload_shouldThrowException() {

        CreateRecurringTransactionDto transactionDto = new CreateRecurringTransactionDto();

        assertThrows(DataIntegrityViolationException.class,
                () -> this.rTransactionService.create(transactionDto)
        );
    }

    @Test
    public void testFindById_validId_shouldReturnRTransaction() {

        assertNotNull(this.rTransactionService.findById(1001L));
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0016,
                () -> this.rTransactionService.findById(2399L));
    }

    @Test
    public void testRecurringTransactionExist_valid_shouldReturnTrue() {
        assertTrue(this.rTransactionService.recurringTransactionExists(1001L, 1001L));
    }

    @Test
    public void testRecurringTransactionExist_invalidRTransactionId_shouldReturnFalse() {
        assertFalse(this.rTransactionService.recurringTransactionExists(10001L, 1001L));
    }

    @Test
    public void testRecurringTransactionExist_invalidBudgetId_shouldReturnFalse() {
        assertFalse(this.rTransactionService.recurringTransactionExists(1001L, 1002L));
    }

    @Test
    public void testAddPrepaymentId_validId_shouldAddPrepaymentId() {

        RecurringTransaction recurringTransaction = this.rTransactionService.findById(1001L);
        assertEquals(1001L, recurringTransaction.getPrepaymentId());

        this.rTransactionService.addPrepaymentId(1002L, recurringTransaction);

        assertEquals(1002L, recurringTransaction.getPrepaymentId());
    }

    @Test
    public void testFindByIdIfExist_validId_shouldReturnRTransaction() {
        assertNotNull(this.rTransactionService.findByIdIfExists(1001L));
    }

    @Test
    public void testFindByIdIfExist_invalidId_shouldReturnOptionalEmpty() {
        assertEquals(Optional.empty(), this.rTransactionService.findByIdIfExists(10001L));
    }

}

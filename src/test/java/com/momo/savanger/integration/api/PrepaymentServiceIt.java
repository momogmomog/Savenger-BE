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
import com.momo.savanger.error.ApiException;
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
public class PrepaymentServiceIt {

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private PrepaymentRepository prepaymentRepository;

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Test
    @Transactional
    public void testCreate_validPayload_shouldCreatePayment() {

        assertEquals(1, this.prepaymentRepository.findAll().size());

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

        assertEquals(2, this.prepaymentRepository.findAll().size());
    }

    @Test
    public void testCreate_emptyPayload_shouldThrowException() {
        CreatePrepaymentDto dto = new CreatePrepaymentDto();

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.prepaymentService.create(dto);
        });

    }

    @Test
    public void testCreate_validPayload_shouldCreateRTransaction(){
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
    public void testCreate_validPayload_shouldUseExistedRTransaction(){
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
        assertThrows(ApiException.class, () -> this.prepaymentService.findById(1002L));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_validPayload_shouldSumAmount() {
        BigDecimal sum = this.prepaymentService.getPrepaymentAmountSumByBudgetId(1001L);

        assertEquals(BigDecimal.valueOf(200.00), sum.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_withoutPrepaymentsBudget_shouldThrowException() {
        BigDecimal sum = this.prepaymentService.getPrepaymentAmountSumByBudgetId(1002L);

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_DOWN), sum.setScale(1, RoundingMode.HALF_DOWN));
    }

}

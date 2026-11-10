package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentQuery;
import com.momo.savanger.api.prepayment.PrepaymentRepository;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.transaction.recurring.RecurringTransaction;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionExecutionService;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionRepository;
import com.momo.savanger.api.transaction.recurring.RecurringTransactionService;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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
public class PrepaymentServiceIt {

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private PrepaymentRepository prepaymentRepository;

    @Autowired
    private RecurringTransactionRepository recurringTransactionRepository;

    @Autowired
    private RecurringTransactionService recurringTransactionService;

    @Autowired
    private RecurringTransactionExecutionService recurringTransactionExecutionService;

    @Test
    @Transactional
    public void testCreate_validPayload_shouldCreatePayment() {

        assertEquals(2, this.prepaymentRepository.findAll().size());

        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        CreateRecurringTransactionDto createRecurringTransactionDto = new CreateRecurringTransactionDto();
        createRecurringTransactionDto.setAmount(BigDecimal.valueOf(20));
        createRecurringTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createRecurringTransactionDto.setType(TransactionType.EXPENSE);
        createRecurringTransactionDto.setBudgetId(1001L);
        createRecurringTransactionDto.setStartFrom(LocalDateTime.now());
        createRecurringTransactionDto.setCategoryId(1001L);
        createRecurringTransactionDto.setAutoExecute(false);

        //Test with new recurring transaction
        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransaction(createRecurringTransactionDto);

        // Act
        var preparymentCreated = this.prepaymentService.create(createPrepaymentDto);

        assertEquals(3, this.prepaymentRepository.findAll().size());

        assertEquals(2, this.recurringTransactionRepository.findAll().size());

        //Test data
        assertEquals(createPrepaymentDto.getName(),
                this.prepaymentRepository.findAll().getFirst().getName());

        assertEquals(createPrepaymentDto.getAmount(),
                this.prepaymentRepository.findAll().getFirst().getAmount());

        assertEquals(createPrepaymentDto.getBudgetId(),
                this.prepaymentRepository.findAll().getFirst().getBudgetId());

        assertEquals(createPrepaymentDto.getPaidUntil(),
                this.prepaymentRepository.findAll().getFirst().getPaidUntil());

        var recurringTransaction = this.recurringTransactionRepository.findAll()
                .stream()
                .filter(rt -> rt.getPrepaymentId().equals(preparymentCreated.getId()))
                .findFirst();

        assertTrue(recurringTransaction.isPresent());
    }

    @Test
    @Transactional
    public void testCreate_withExistingRTransactionId_shouldCreatePrepaymentWithExistingRTransaction() {

        assertEquals(2, this.prepaymentRepository.findAll().size());

        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        assertEquals(1001L, this.recurringTransactionService.findById(9001L).getPrepaymentId());

        //Test with existing recurring transaction id
        CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransactionId(9001L);

        final Prepayment prepayment = this.prepaymentService.create(createPrepaymentDto);

        assertEquals(3, this.prepaymentRepository.findAll().size());

        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        //Test addPrepaymentId method
        assertNotEquals(1001L, this.recurringTransactionService.findById(9001L).getPrepaymentId());

        //Test prepaymentId in rTransaction is correct after change
        final RecurringTransaction recurringTransaction = this.recurringTransactionService.findById(
                9001L);
        assertEquals(prepayment.getId(), recurringTransaction.getPrepaymentId());
    }

    @Test
    @Transactional
    public void testCreate_emptyPayload_shouldThrowException() {
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.prepaymentService.create(dto);
        });

    }

    @Test
    public void testFindById_validPayload_shouldFindPayment() {
        final Prepayment prepayment = this.prepaymentService.findById(1001L);

        assertNotNull(prepayment);
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0015,
                () -> this.prepaymentService.findById(10040L));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_validPayload_shouldSumAmount() {
        final BigDecimal sum = this.prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(
                1001L);

        assertEquals(BigDecimal.valueOf(200.00), sum.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_withTwoPrepayments_shouldSumAmount() {
        final CreatePrepaymentDto createPrepaymentDto = new CreatePrepaymentDto();
        createPrepaymentDto.setAmount(BigDecimal.valueOf(200));
        createPrepaymentDto.setBudgetId(1001L);
        createPrepaymentDto.setName("NETI");
        createPrepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        createPrepaymentDto.setRecurringTransactionId(9001L);

        this.prepaymentService.create(createPrepaymentDto);

        final BigDecimal sum = this.prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(
                1001L);

        assertEquals(BigDecimal.valueOf(400.00), sum.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_withoutPrepaymentsBudget_shouldThrowException() {
        final BigDecimal sum = this.prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(
                1003L);

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_DOWN),
                sum.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testPrepaymentAmountSumByBudgetId_invalidId() {
        final BigDecimal sum = this.prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(
                10099L);

        assertEquals(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_DOWN),
                sum.setScale(1, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testFindByIdIfExist_validId_shouldReturnPrepayment() {
        final Optional<Prepayment> prepayment = this.prepaymentService.findByIdIfExists(1001L);

        assertTrue(prepayment.isPresent());
        assertEquals(1001L, prepayment.get().getId());
    }

    @Test
    public void testFindByIdIfExist_invalidId_shouldReturnOptionalEmpty() {
        assertEquals(Optional.empty(), this.prepaymentService.findByIdIfExists(10001L));
    }

    @Test
    @Transactional
    public void testFindByIdIfExist_completedPrepayment_shouldRespectAllowCompletedFlag() {
        this.recurringTransactionExecutionService.payPrepayment(9001L);
        this.recurringTransactionExecutionService.payPrepayment(9001L);

        assertEquals(Optional.empty(), this.prepaymentService.findByIdIfExists(1001L));
        assertEquals(Optional.empty(), this.prepaymentService.findByIdIfExists(1001L, false));
        assertTrue(this.prepaymentService.findByIdIfExists(1001L, true).isPresent());
    }

    @Test
    @Transactional
    public void testEdit_validPayload_shouldUpdatePrepayment() {
        final LocalDateTime newPaidUntil = LocalDateTime.of(2026, 6, 1, 12, 0);

        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(300));
        dto.setBudgetId(1001L);
        dto.setPaidUntil(newPaidUntil);
        dto.setRecurringTransactionId(9001L);

        final Prepayment edited = this.prepaymentService.edit(1001L, dto);

        assertNotNull(edited);
        assertEquals(1001L, edited.getId());
        assertEquals("Updated netsurf", edited.getName());
        assertEquals(BigDecimal.valueOf(300).setScale(2, RoundingMode.HALF_DOWN),
                edited.getAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(300).setScale(2, RoundingMode.HALF_DOWN),
                edited.getRemainingAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(newPaidUntil, edited.getPaidUntil());
        assertFalse(edited.getCompleted());
    }

    @Test
    @Transactional
    public void testEdit_afterPartialPayment_shouldRecalculateRemainingAmountWhenPositive() {
        // Pay once (101 paid out of 200)
        this.recurringTransactionExecutionService.payPrepayment(9001L);

        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(250));
        dto.setBudgetId(1001L);
        dto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        dto.setRecurringTransactionId(9001L);

        final Prepayment edited = this.prepaymentService.edit(1001L, dto);

        // 250 - 101 = 149 remaining (> 0)
        assertEquals(BigDecimal.valueOf(149).setScale(2, RoundingMode.HALF_DOWN),
                edited.getRemainingAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertFalse(edited.getCompleted());
        assertFalse(this.recurringTransactionService.findById(9001L).getCompleted());
    }

    @Test
    @Transactional
    public void testEdit_afterPartialPayment_whenNewAmountEqualsPaidAmount_shouldCompletePrepayment() {
        // Pay once (101 paid out of 200)
        this.recurringTransactionExecutionService.payPrepayment(9001L);

        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(101));
        dto.setBudgetId(1001L);
        dto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        dto.setRecurringTransactionId(9001L);

        // 101 - 101 = 0 remaining -> remainingAmount = 0 and completed = true
        final Prepayment edited = this.prepaymentService.edit(1001L, dto);

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                edited.getRemainingAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertTrue(edited.getCompleted());
        assertTrue(this.recurringTransactionService.findById(9001L).getCompleted());
    }

    @Test
    @Transactional
    public void testEdit_afterPartialPayment_whenNewAmountLessThanPaidAmount_shouldSetRemainingToZeroAndComplete() {
        // Pay once (101 paid out of 200)
        this.recurringTransactionExecutionService.payPrepayment(9001L);

        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(80));
        dto.setBudgetId(1001L);
        dto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        dto.setRecurringTransactionId(9001L);

        // 80 - 101 = -21 (< 0) -> remainingAmount = 0 and completed = true
        final Prepayment edited = this.prepaymentService.edit(1001L, dto);

        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                edited.getRemainingAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertTrue(edited.getCompleted());
        assertTrue(this.recurringTransactionService.findById(9001L).getCompleted());
    }

    @Test
    @Transactional
    public void testEdit_withNewRecurringTransaction_shouldCreateAndLinkRecurringTransaction() {
        final CreateRecurringTransactionDto createRecurringTransactionDto = new CreateRecurringTransactionDto();
        createRecurringTransactionDto.setAmount(BigDecimal.valueOf(50));
        createRecurringTransactionDto.setRecurringRule("FREQ=MONTHLY;INTERVAL=1");
        createRecurringTransactionDto.setType(TransactionType.EXPENSE);
        createRecurringTransactionDto.setBudgetId(1001L);
        createRecurringTransactionDto.setStartFrom(LocalDateTime.now());
        createRecurringTransactionDto.setCategoryId(1001L);
        createRecurringTransactionDto.setAutoExecute(false);

        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(250));
        dto.setBudgetId(1001L);
        dto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        dto.setRecurringTransaction(createRecurringTransactionDto);

        assertEquals(1, this.recurringTransactionRepository.findAll().size());

        final Prepayment edited = this.prepaymentService.edit(1001L, dto);

        assertEquals(2, this.recurringTransactionRepository.findAll().size());
        final var newRecurring = this.recurringTransactionRepository.findAll().stream()
                .filter(rt -> !rt.getId().equals(9001L))
                .findFirst()
                .orElseThrow();
        assertEquals(edited.getId(), newRecurring.getPrepaymentId());
        assertFalse(newRecurring.getCompleted());
    }

    @Test
    public void testEdit_invalidBudgetId_shouldThrowException() {
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(200));
        dto.setBudgetId(1002L);
        dto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        dto.setRecurringTransactionId(9001L);

        AssertUtil.assertApiException(ApiErrorCode.ERR_0024,
                () -> this.prepaymentService.edit(1001L, dto));
    }

    @Test
    public void testEdit_invalidId_shouldThrowException() {
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setName("Updated netsurf");
        dto.setAmount(BigDecimal.valueOf(200));
        dto.setBudgetId(1001L);
        dto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        dto.setRecurringTransactionId(9001L);

        AssertUtil.assertApiException(ApiErrorCode.ERR_0015,
                () -> this.prepaymentService.edit(99999L, dto));
    }

    @Test
    public void testSearch_validQuery_shouldReturnMatchingPrepayments() {
        final PrepaymentQuery query = new PrepaymentQuery();
        query.setPage(new PageQuery(0, 10));
        query.setSort(new SortQuery("id", SortDirection.ASC));
        query.setBudgetId(1001L);
        query.setName("netsurf");
        query.setCompleted(false);
        query.setAmount(new BetweenQuery<>(BigDecimal.valueOf(100), BigDecimal.valueOf(300)));
        query.setRemainingAmount(
                new BetweenQuery<>(BigDecimal.valueOf(100), BigDecimal.valueOf(300))
        );
        query.setPaidUntil(
                new BetweenQuery<>(
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 1, 1, 0, 0)
                )
        );

        final Page<Prepayment> result = this.prepaymentService.search(query);

        assertEquals(1, result.getTotalElements());
        assertEquals(1001L, result.getContent().getFirst().getId());

        query.setName("nonexistent");
        assertEquals(0, this.prepaymentService.search(query).getTotalElements());
    }

}

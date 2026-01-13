package com.momo.savanger.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetRepository;
import com.momo.savanger.api.budget.BudgetServiceImpl;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.TransactionService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetStatisticsTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private PrepaymentService prepaymentService;

    @Mock
    private BudgetRepository budgetRepository;

    @InjectMocks
    private BudgetServiceImpl budgetService;

    Budget testBudget;

    @BeforeEach
    void setUp() {
        testBudget = new Budget();
        testBudget.setId(1L);
        testBudget.setBudgetName("test");
        testBudget.setDateStarted(LocalDateTime.now());
        testBudget.setOwnerId(1L);
        testBudget.setBalance(BigDecimal.ZERO);
        testBudget.setBudgetCap(BigDecimal.valueOf(1000));
        testBudget.setActive(true);

        when(budgetRepository.findById(1L)).thenReturn(Optional.of(testBudget));
    }

    @Test
    public void testBudgetStatistic_empty_shouldReturnZero() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.ZERO);

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.ZERO, stats.getEarningsAmount());
        assertEquals(BigDecimal.ZERO, stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.ZERO, stats.getRealBalance());
        assertEquals(BigDecimal.ZERO, stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withExpense() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.ZERO);

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.ZERO, stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());

        assertEquals(BigDecimal.valueOf(-200), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(-200), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withEarnings() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.ZERO);

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.ZERO, stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(1000), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(1000), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withEarningsAndExpenses() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.ZERO);

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(800), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(800), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withPrepayment() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.valueOf(230));

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(570), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(800), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withPrepaymentAndInitialBalance() {
        Long budgetId = 1L;

        testBudget.setBalance(BigDecimal.valueOf(1000));
        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.valueOf(230));

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(1570), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(1800), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withReceivedDebt() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.valueOf(50));
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.valueOf(230));

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtLendedAmount());
        assertEquals(BigDecimal.valueOf(50), stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(570), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(750), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withLendedDebt() {
        Long budgetId = 1L;

        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.valueOf(100));
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.ZERO);
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.valueOf(230));

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.valueOf(100), stats.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(570), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(900), stats.getBalance());
    }

    @Test
    public void testBudgetStatistic_withReceivedAndLendedDebt() {
        Long budgetId = 1L;

        // Balance: 1000, Real Balance: 1000
        when(transactionService.getEarningsAmount(budgetId)).thenReturn(BigDecimal.valueOf(1000));

        // Balance 800, Real Balance 800
        when(transactionService.getExpensesAmount(budgetId)).thenReturn(BigDecimal.valueOf(200));

        // Balance 800, Real Balance 700
        when(transactionService.getDebtLendedAmount(budgetId)).thenReturn(BigDecimal.valueOf(100));

        // Balance 800, Real Balance 750
        when(transactionService.getDebtReceivedAmount(budgetId)).thenReturn(BigDecimal.valueOf(50));

        // Balance 800, Real Balance 520
        when(prepaymentService.getRemainingPrepaymentAmountSumByBudgetId(budgetId)).thenReturn(
                BigDecimal.valueOf(230));

        BudgetStatistics stats = budgetService.getStatistics(budgetId);

        assertNotNull(stats);
        assertEquals(BigDecimal.valueOf(1000), stats.getEarningsAmount());
        assertEquals(BigDecimal.valueOf(200), stats.getExpensesAmount());
        assertEquals(BigDecimal.valueOf(100), stats.getDebtLendedAmount());
        assertEquals(BigDecimal.valueOf(50), stats.getDebtReceivedAmount());
        assertEquals(BigDecimal.valueOf(520), stats.getRealBalance());
        assertEquals(BigDecimal.valueOf(800), stats.getBalance());
    }
}

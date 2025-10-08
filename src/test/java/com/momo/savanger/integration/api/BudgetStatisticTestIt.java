package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.api.debt.PayDebtDto;
import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.Prepayment;
import com.momo.savanger.api.prepayment.PrepaymentService;
import com.momo.savanger.api.transaction.Transaction;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.integration.web.Constants;
import com.momo.savanger.integration.web.WithLocalMockedUser;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@AutoConfigureMockMvc
@Sql(value = "classpath:/sql/prepayment/del-recurring_transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-prepayment-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetStatisticTestIt {

    public static BigDecimal BUDGET_ONE_BALANCE = BigDecimal.ZERO;
    public static BigDecimal BUDGET_TWO_BALANCE = BigDecimal.valueOf(100);
    public static BigDecimal BUDGET_THREE_BALANCE = BigDecimal.valueOf(200);

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private DebtService debtService;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        this.createBudget("Chocolate", BUDGET_ONE_BALANCE);
        this.createBudget("Sol", BUDGET_TWO_BALANCE);
        this.createBudget("Knigi", BUDGET_THREE_BALANCE);
    }

    //TODO: Add tests for prepayment transaction when rRule is ready
    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testBudgetStatistic_firstBudgetWithBalanceZero() {
        BudgetStatistics budgetStatistics = this.budgetService.getStatistics(1L);

        //Test without debts, transactions and prepayments
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with received debt

        Debt debtReceived = this.createDebt(3L, 1L, BigDecimal.valueOf(30));

        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with received and lend debt

        Debt debtLend = this.createDebt(1L, 2L, BigDecimal.valueOf(20));

        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt

        debtReceived = this.createDebt(3L, 1L, BigDecimal.valueOf(50));

        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(60).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt and expense and earning transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10), 1L);
        this.createTransaction(TransactionType.INCOME, BigDecimal.valueOf(500), 1L);

        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(490).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(550).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 prepayment

        this.createPrepayment(BigDecimal.valueOf(25), "netinet", 1L);

        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(490).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(525).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test pay debt
        this.payDebt(debtReceived.getId(), BigDecimal.valueOf(20));
        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(490).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(505).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 income prepayment
        // and one expense prepayment

        this.createPrepayment(BigDecimal.valueOf(30), "parno", 1L);
        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(490).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(475).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Receiver pay to you

        this.payDebt(debtLend.getId(), BigDecimal.TEN);
        budgetStatistics = this.budgetService.getStatistics(1L);

        assertEquals(BigDecimal.valueOf(490).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(485).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(90).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testBudgetStatistic_secondBudgetWithBalanceOneHundred() {
        BudgetStatistics budgetStatistics = this.budgetService.getStatistics(2L);

        //Test without debts, transactions and prepayments
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with received debt

        Debt debtReceived = this.createDebt(3L, 2L, BigDecimal.valueOf(30));

        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(130).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with received and lend debt

        Debt debtLend = this.createDebt(2L, 1L, BigDecimal.valueOf(20));

        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(110).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt

        debtReceived = this.createDebt(3L, 2L, BigDecimal.valueOf(50));

        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(160).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt and expense and earning transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10), 2L);
        this.createTransaction(TransactionType.INCOME, BigDecimal.valueOf(500), 2L);

        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(650).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 prepayment

        this.createPrepayment(BigDecimal.valueOf(25), "netinet", 2L);

        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(625).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(30).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test pay debt
        this.payDebt(debtReceived.getId(), BigDecimal.valueOf(20));
        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(605).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 income prepayment
        // and one expense prepayment

        this.createPrepayment(BigDecimal.valueOf(30), "parno", 2L);
        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(575).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(80).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(580).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        //Receiver pay to you

        this.payDebt(debtLend.getId(), BigDecimal.TEN);
        budgetStatistics = this.budgetService.getStatistics(2L);

        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN)
                , budgetStatistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(585).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(90).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(590).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budgetStatistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
    }

    private void payDebt(Long debtId, BigDecimal amount) {
        PayDebtDto payDebtDto = new PayDebtDto();
        payDebtDto.setAmount(amount);

        this.debtService.pay(debtId, payDebtDto);
    }

    private Budget createBudget(String name, BigDecimal balance) {
        CreateBudgetDto budgetDto = new CreateBudgetDto();
        budgetDto.setBudgetName(name);
        budgetDto.setBalance(balance);
        budgetDto.setActive(true);
        budgetDto.setAutoRevise(false);
        budgetDto.setDateStarted(LocalDateTime.now());
        budgetDto.setDueDate(LocalDateTime.now().plusMonths(8));
        budgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");

        return this.budgetService.create(budgetDto, 1L);
    }

    private Debt createDebt(Long lenderId, Long receiverId, BigDecimal amount) {
        CreateDebtDto debtDto = new CreateDebtDto();
        debtDto.setReceiverBudgetId(receiverId);
        debtDto.setLenderBudgetId(lenderId);
        debtDto.setDebtAmount(amount);

        return this.debtService.create(debtDto);
    }

    private Prepayment createPrepayment(BigDecimal amount, String name, Long budgetId) {
        CreateRecurringTransactionDto rTransactionDto = this.createRecurringTransactionDto(
                TransactionType.EXPENSE, amount, budgetId
        );

        CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();
        prepaymentDto.setAmount(amount);
        prepaymentDto.setName(name);
        prepaymentDto.setBudgetId(budgetId);
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(3));
        prepaymentDto.setRecurringTransaction(rTransactionDto);

        return this.prepaymentService.create(prepaymentDto);
    }

    private CreateRecurringTransactionDto createRecurringTransactionDto(TransactionType type
            , BigDecimal amount, Long budgetId
    ) {
        CreateRecurringTransactionDto recurringTransactionDto = new CreateRecurringTransactionDto();
        recurringTransactionDto.setType(type);
        recurringTransactionDto.setBudgetId(budgetId);
        recurringTransactionDto.setAmount(amount);
        recurringTransactionDto.setAutoExecute(false);
        recurringTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");

        return recurringTransactionDto;
    }

    private Transaction createTransaction(TransactionType type
            , BigDecimal amount, Long budgetId) {
        CreateTransactionDto transactionDto = new CreateTransactionDto();
        transactionDto.setType(type);
        transactionDto.setBudgetId(budgetId);
        transactionDto.setAmount(amount);

        return this.transactionService.create(transactionDto, 1L);
    }

}

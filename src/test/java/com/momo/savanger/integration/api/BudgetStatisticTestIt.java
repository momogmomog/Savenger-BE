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
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.integration.web.Constants;
import com.momo.savanger.integration.web.WithLocalMockedUser;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.function.Supplier;
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
@Sql(value = "classpath:/sql/user-it-data.sql")
@Sql(value = "classpath:/sql/prepayment/del-recurring_transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-prepayment-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/prepayment/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetStatisticTestIt {

    public static BigDecimal BUDGET_ONE_BALANCE = BigDecimal.ZERO;
    public static BigDecimal BUDGET_TWO_BALANCE = BigDecimal.valueOf(100);
    public static BigDecimal BUDGET_THREE_BALANCE = BigDecimal.valueOf(200);
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private DebtService debtService;

    @Autowired
    private PrepaymentService prepaymentService;

    @Autowired
    private TransactionService transactionService;

    Long budgetOneId;
    Long budgetTwoId;
    Long budgetThreeId;

    @BeforeEach
    void setUp() {
        Budget budgetOne = this.createBudget("Chocolate", BUDGET_ONE_BALANCE);
        budgetOneId = budgetOne.getId();

        Budget budgetTwo = this.createBudget("Sol", BUDGET_TWO_BALANCE);
        budgetTwoId = budgetTwo.getId();

        Budget budgetThree = this.createBudget("Knigi", BUDGET_THREE_BALANCE);
        budgetThreeId = budgetThree.getId();
    }

    //TODO: Add tests for prepayment transaction when rRule is ready
    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testBudgetStatistic_firstBudgetWithBalanceZero() {
        final Supplier<BudgetStatistics> getB1Stat = () -> this.budgetService.getStatistics(this.budgetOneId);

        BudgetStatistics b1Stat = getB1Stat.get();

        //Test without debts, transactions and prepayments
        assertEquals(
                ZERO,
                toScale(b1Stat.getBalance())
        );
        assertEquals(
                ZERO,
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                ZERO,
                toScale(b1Stat.getDebtLendedAmount())
        );
        assertEquals(
                ZERO,
                toScale(b1Stat.getDebtReceivedAmount())
        );
        assertEquals(
                ZERO,
                toScale(b1Stat.getEarningsAmount())
        );
        assertEquals(
                ZERO,
                toScale(b1Stat.getExpensesAmount()).setScale(2, RoundingMode.HALF_DOWN)
        );

        //Test with received debt

        Debt b3ToB1 = this.createDebt(this.budgetThreeId, this.budgetOneId, BigDecimal.valueOf(30));

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(0)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(30),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(0),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(30),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(0),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(0),
                toScale(b1Stat.getExpensesAmount())
        );

        //Test with received and lend debt

        Debt b1ToB2 = this.createDebt(budgetOneId, budgetTwoId, BigDecimal.valueOf(20));

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(0)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(30),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(0),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(0),
                toScale(b1Stat.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt

        b3ToB1 = this.createDebt(budgetThreeId, budgetOneId, BigDecimal.valueOf(50));

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(0)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(60),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(0),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(0),
                toScale(b1Stat.getExpensesAmount())
        );
        //Test with 2 received and 1 lend debt and expense and earning transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10), budgetOneId);
        this.createTransaction(TransactionType.INCOME, BigDecimal.valueOf(500), budgetOneId);

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(490)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(550),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(10),
                toScale(b1Stat.getExpensesAmount())
        );

        //Test with 2 received and 1 lent debt, 1 expense and 1 earning transaction, 1 prepayment

        this.createPrepayment(BigDecimal.valueOf(25), "netinet", budgetOneId);

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(490)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(525),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(10),
                toScale(b1Stat.getExpensesAmount())
        );

        //Test pay debt
        this.payDebt(b3ToB1.getId(), BigDecimal.valueOf(20));
        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(490)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(505),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(10),
                toScale(b1Stat.getExpensesAmount())
        );

        //Receiver pay to you

        this.payDebt(b1ToB2.getId(), BigDecimal.TEN);
        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(490)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(515),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(10),
                toScale(b1Stat.getExpensesAmount())
        );

        // Test transaction with real numbers

        this.createTransaction(
                TransactionType.EXPENSE,
                BigDecimal.valueOf(5.79),
                budgetOneId
        );

        this.createTransaction(
                TransactionType.EXPENSE,
                BigDecimal.valueOf(4.71000123),
                budgetOneId
        );

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(479.50)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(504.5),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(20.50),
                toScale(b1Stat.getExpensesAmount())
        );

        //Test what if realBalance is 0

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(504.5), budgetOneId);

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(-25)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(0),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(525),
                toScale(b1Stat.getExpensesAmount())
        );

        //Real balance is 0, now create expense transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.TEN, budgetOneId);

        b1Stat = getB1Stat.get();

        assertEquals(
                toScale(-35)
                , toScale(b1Stat.getBalance())
        );
        assertEquals(
                toScale(-10),
                toScale(b1Stat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b1Stat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b1Stat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b1Stat.getEarningsAmount())
        );

        assertEquals(
                toScale(535),
                toScale(b1Stat.getExpensesAmount())
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testBudgetStatistic_secondBudgetWithBalanceOneHundred() {
        final Supplier<BudgetStatistics> getB2Stat = () -> this.budgetService.getStatistics(this.budgetTwoId);

        BudgetStatistics b2sStat = getB2Stat.get();

        //Test without debts, transactions and prepayments

        assertEquals(
                toScale(100),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(100),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test with received debt

        Debt b3ToB2 = this.createDebt(budgetThreeId, budgetTwoId, BigDecimal.valueOf(30));

        b2sStat = this.budgetService.getStatistics(budgetTwoId);

        assertEquals(
                toScale(100),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(130),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(30),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test with received and lend debt

        Debt b2ToB1 = this.createDebt(budgetTwoId, budgetOneId, BigDecimal.valueOf(20));

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(100),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(110),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(30),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test with 2 received and 1 lent debt

        b3ToB2 = this.createDebt(budgetThreeId, budgetTwoId, BigDecimal.valueOf(50));

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(100),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(160),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt and expense and earning transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10), budgetTwoId);
        this.createTransaction(TransactionType.INCOME, BigDecimal.valueOf(500), budgetTwoId);

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(590),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(650),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 prepayment

        this.createPrepayment(BigDecimal.valueOf(25), "netinet", budgetTwoId);

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(590),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(625),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test pay debt
        this.payDebt(b3ToB2.getId(), BigDecimal.valueOf(20));
        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(590),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(605),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(60),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getExpensesAmount())
        );

        //Receiver pay to you

        this.payDebt(b2ToB1.getId(), BigDecimal.TEN);
        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(590),
                toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(615),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getDebtLendedAmount())
        );
        assertEquals(
                toScale(60),
                toScale(b2sStat.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getExpensesAmount())
        );

        // Test transaction with real number

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10.50), budgetTwoId);

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(579.50)
                , toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(604.5),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b2sStat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );

        assertEquals(
                toScale(20.50),
                toScale(b2sStat.getExpensesAmount())
        );

        //Test what if realBalance is 0

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(604.5), budgetTwoId);

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(-25)
                , toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(0),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b2sStat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );

        assertEquals(
                toScale(625),
                toScale(b2sStat.getExpensesAmount())
        );

        //Real balance is 0, now create expense transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.TEN, budgetTwoId);

        b2sStat = getB2Stat.get();

        assertEquals(
                toScale(-35)
                , toScale(b2sStat.getBalance())
        );
        assertEquals(
                toScale(-10),
                toScale(b2sStat.getRealBalance())
        );
        assertEquals(
                toScale(10),
                toScale(b2sStat.getDebtLendedAmount())
        );

        assertEquals(
                toScale(60),
                toScale(b2sStat.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(500),
                toScale(b2sStat.getEarningsAmount())
        );

        assertEquals(
                toScale(635),
                toScale(b2sStat.getExpensesAmount())
        );
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
        CreateTransactionServiceDto transactionDto = new CreateTransactionServiceDto();
        transactionDto.setType(type);
        transactionDto.setBudgetId(budgetId);
        transactionDto.setAmount(amount);

        return this.transactionService.create(transactionDto, 1L);
    }

    private BigDecimal toScale(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_DOWN);
    }

    private BigDecimal toScale(Double amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_DOWN);
    }

    private BigDecimal toScale(Integer amount) {
        return BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_DOWN);
    }
}

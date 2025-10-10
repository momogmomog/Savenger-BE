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
    public static Long BUDGET_ONE_ID = 1L;
    public static Long BUDGET_TWO_ID = 2L;
    public static Long BUDGET_THREE_ID = 3L;
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN);

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
        Budget budgetOne = this.createBudget("Chocolate", BUDGET_ONE_BALANCE);
        Budget budgetTwo = this.createBudget("Sol", BUDGET_TWO_BALANCE);
        Budget budgetThree = this.createBudget("Knigi", BUDGET_THREE_BALANCE);

        budgetOne.setId(BUDGET_ONE_ID);
        budgetTwo.setId(BUDGET_TWO_ID);
        budgetThree.setId(BUDGET_THREE_ID);
    }

    //TODO: Add tests for prepayment transaction when rRule is ready
    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testBudgetStatistic_firstBudgetWithBalanceZero() {
        BudgetStatistics budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        //Test without debts, transactions and prepayments
        assertEquals(
                ZERO,
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                ZERO,
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                ZERO,
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                ZERO,
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                ZERO,
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                ZERO,
                toScale(budgetStatistics.getExpensesAmount()).setScale(2, RoundingMode.HALF_DOWN)
        );

        //Test with received debt

        Debt debtReceived = this.createDebt(BUDGET_THREE_ID, BUDGET_ONE_ID, BigDecimal.valueOf(30));

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(0)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with received and lend debt

        Debt debtLend = this.createDebt(BUDGET_ONE_ID, BUDGET_TWO_ID, BigDecimal.valueOf(20));

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(0)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(10),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt

        debtReceived = this.createDebt(BUDGET_THREE_ID, BUDGET_ONE_ID, BigDecimal.valueOf(50));

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(0)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(60),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getExpensesAmount())
        );
        //Test with 2 received and 1 lend debt and expense and earning transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10), BUDGET_ONE_ID);
        this.createTransaction(TransactionType.INCOME, BigDecimal.valueOf(500), BUDGET_ONE_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(490)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(550),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 prepayment

        this.createPrepayment(BigDecimal.valueOf(25), "netinet", BUDGET_ONE_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(490)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(525),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test pay debt
        this.payDebt(debtReceived.getId(), BigDecimal.valueOf(20));
        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(490)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(505),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 income prepayment
        // and one expense prepayment

        this.createPrepayment(BigDecimal.valueOf(30), "parno", BUDGET_ONE_ID);
        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(490)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(475),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Receiver pay to you

        this.payDebt(debtLend.getId(), BigDecimal.TEN);
        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(490)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(485),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        // Test transaction with real number

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10.50), BUDGET_ONE_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(479.50)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(474.50),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(60.50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test what if realBalance is 0

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(474.50), BUDGET_ONE_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(5)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(535),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Real balance is 0, now create expense transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.TEN, BUDGET_ONE_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_ONE_ID);

        assertEquals(
                toScale(-5)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(-10),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(545),
                toScale(budgetStatistics.getExpensesAmount())
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testBudgetStatistic_secondBudgetWithBalanceOneHundred() {
        BudgetStatistics budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        //Test without debts, transactions and prepayments

        assertEquals(
                toScale(100),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(100),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with received debt

        Debt debtReceived = this.createDebt(BUDGET_THREE_ID, BUDGET_TWO_ID, BigDecimal.valueOf(30));

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(100),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(130),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with received and lend debt

        Debt debtLend = this.createDebt(BUDGET_TWO_ID, BUDGET_ONE_ID, BigDecimal.valueOf(20));

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(100),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(110),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt

        debtReceived = this.createDebt(BUDGET_THREE_ID, BUDGET_TWO_ID, BigDecimal.valueOf(50));

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(100),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(160),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt and expense and earning transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10), BUDGET_TWO_ID);
        this.createTransaction(TransactionType.INCOME, BigDecimal.valueOf(500), BUDGET_TWO_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(650),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 prepayment

        this.createPrepayment(BigDecimal.valueOf(25), "netinet", BUDGET_TWO_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(625),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(20),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(30),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test pay debt
        this.payDebt(debtReceived.getId(), BigDecimal.valueOf(20));
        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(605),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test with 2 received and 1 lend debt, 1 expense and 1 earning transaction, 1 income prepayment
        // and one expense prepayment

        this.createPrepayment(BigDecimal.valueOf(30), "parno", BUDGET_TWO_ID);
        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(575),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(80),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(580),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Receiver pay to you

        this.payDebt(debtLend.getId(), BigDecimal.TEN);
        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(585),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );
        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );
        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );
        assertEquals(
                toScale(50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        // Test transaction with real number

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(10.50), BUDGET_TWO_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(579.50)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(574.50),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(60.50),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Test what if realBalance is 0

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.valueOf(574.50), BUDGET_TWO_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(5)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(0),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(635),
                toScale(budgetStatistics.getExpensesAmount())
        );

        //Real balance is 0, now create expense transaction

        this.createTransaction(TransactionType.EXPENSE, BigDecimal.TEN, BUDGET_TWO_ID);

        budgetStatistics = this.budgetService.getStatistics(BUDGET_TWO_ID);

        assertEquals(
                toScale(-5)
                , toScale(budgetStatistics.getBalance())
        );
        assertEquals(
                toScale(-10),
                toScale(budgetStatistics.getRealBalance())
        );
        assertEquals(
                toScale(40),
                toScale(budgetStatistics.getDebtLendedAmount())
        );

        assertEquals(
                toScale(90),
                toScale(budgetStatistics.getDebtReceivedAmount())
        );

        assertEquals(
                toScale(590),
                toScale(budgetStatistics.getEarningsAmount())
        );

        assertEquals(
                toScale(645),
                toScale(budgetStatistics.getExpensesAmount())
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

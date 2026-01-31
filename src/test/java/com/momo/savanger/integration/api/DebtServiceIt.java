package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.api.debt.Debt;
import com.momo.savanger.api.debt.DebtRepository;
import com.momo.savanger.api.debt.DebtService;
import com.momo.savanger.api.debt.PayDebtDto;
import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.integration.web.Constants;
import com.momo.savanger.integration.web.WithLocalMockedUser;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/debt/budget-it-data.sql")
@Sql("classpath:/sql/debt/tag-it-data.sql")
@Sql("classpath:/sql/debt/category-it-data.sql")
@Sql("classpath:/sql/debt/transaction-it-data.sql")
@Sql("classpath:/sql/debt/transactions_tags-it-data.sql")
@Sql("classpath:/sql/debt/revision-it-data.sql")
@Sql("classpath:/sql/debt/debt-it-data.sql")
@Sql(value = "classpath:/sql/debt/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class DebtServiceIt {

    @Autowired
    private DebtService debtService;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    TransactionService transactionService;

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_newDebtReceiverAndLender_shouldCreateDebt() {

        //Test debts size before
        assertEquals(1, debtRepository.findAll().size());

        CreateDebtDto dto = new CreateDebtDto();

        dto.setDebtAmount(BigDecimal.valueOf(200));
        dto.setLenderBudgetId(1001L);
        dto.setReceiverBudgetId(1002L);

        BudgetStatistics lenderBudget = this.budgetService.getStatistics(dto.getLenderBudgetId());
        BudgetStatistics receiverBudget = this.budgetService.getStatistics(
                dto.getReceiverBudgetId());

        //Test budgetStatistics for lender budget before debt

        assertEquals(BigDecimal.valueOf(320).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(320.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        //Test budgetStatistics for receiver budget before debt

        assertEquals(BigDecimal.valueOf(243).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(243.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        Debt debt = this.debtService.create(dto);

        lenderBudget = this.budgetService.getStatistics(dto.getLenderBudgetId());
        receiverBudget = this.budgetService.getStatistics(dto.getReceiverBudgetId());

        //Test debt amount
        assertEquals(BigDecimal.valueOf(200), debt.getAmount());

        //Test budgetStatistics for lender budget after debt
        assertEquals(BigDecimal.valueOf(320).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getBalance().setScale(2, RoundingMode.HALF_DOWN));

        // Lender doesn't register expense
        assertEquals(BigDecimal.valueOf(1).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(120.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        //Test budgetStatistics for receiver budget

        assertEquals(BigDecimal.valueOf(243).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));

        // Receiver doesn't register earning
        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(443.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        // Test debt size after
        assertEquals(2, debtRepository.findAll().size());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_debtWithExistLenderAndReceiver_shouldUpdateDebt() {

        //Test debt amount before create
        Debt debt = this.debtService.findDebt(1001L, 1002L).orElse(null);
        assertNotNull(debt);
        assertEquals(BigDecimal.valueOf(302.09),
                debt.getAmount().setScale(2, RoundingMode.HALF_DOWN));

        CreateDebtDto dto = new CreateDebtDto();
        dto.setDebtAmount(BigDecimal.valueOf(200));
        dto.setLenderBudgetId(1001L);
        dto.setReceiverBudgetId(1002L);

        this.debtService.create(dto);

        assertEquals(2, debtRepository.findAll().size());

        BudgetStatistics lenderBudget = this.budgetService.getStatistics(dto.getLenderBudgetId());
        BudgetStatistics receiverBudget = this.budgetService.getStatistics(
                dto.getReceiverBudgetId());

        //Test statistics before
        assertEquals(BigDecimal.valueOf(200.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(443.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(120.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        //Test create debt already exist
        dto.setDebtAmount(BigDecimal.valueOf(20));
        debt = this.debtService.create(dto);

        //Test debt amount after create
        assertEquals(
                BigDecimal.valueOf(220.00).setScale(2, RoundingMode.HALF_DOWN),
                debt.getAmount().setScale(2, RoundingMode.HALF_DOWN)
        );

        // assert size of debts did not change (debt was updated)
        assertEquals(
                2, debtRepository.findAll().size()
        );

        lenderBudget = this.budgetService.getStatistics(dto.getLenderBudgetId());
        receiverBudget = this.budgetService.getStatistics(dto.getReceiverBudgetId());

        assertEquals(
                BigDecimal.valueOf(220.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN)
        );
        assertEquals(
                BigDecimal.valueOf(463.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN)
        );

        assertEquals(
                BigDecimal.valueOf(220).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getDebtLendedAmount().setScale(2, RoundingMode.HALF_DOWN)
        );
        assertEquals(
                BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN)
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_twoDebtsWithSameReceiver() {

        CreateDebtDto dto = new CreateDebtDto();
        dto.setDebtAmount(BigDecimal.valueOf(20));
        dto.setLenderBudgetId(1001L);
        dto.setReceiverBudgetId(1002L);
        this.debtService.create(dto);

        assertEquals(2, debtRepository.findAll().size());

        // Test budget can be in more than one debt
        dto.setLenderBudgetId(1003L);

        this.debtService.create(dto);

        BudgetStatistics lenderBudget = this.budgetService.getStatistics(dto.getLenderBudgetId());
        BudgetStatistics receiverBudget = this.budgetService.getStatistics(
                dto.getReceiverBudgetId());

        assertEquals(3, debtRepository.findAll().size());

        lenderBudget = this.budgetService.getStatistics(dto.getLenderBudgetId());
        receiverBudget = this.budgetService.getStatistics(dto.getReceiverBudgetId());

        assertEquals(BigDecimal.valueOf(40).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getDebtReceivedAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(283.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(0).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(480).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudget.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreateDebtTransactions() {
        CreateDebtDto dto = new CreateDebtDto();
        dto.setDebtAmount(BigDecimal.valueOf(20));
        dto.setLenderBudgetId(1001L);
        dto.setReceiverBudgetId(1002L);

        this.debtService.create(dto);

        BigDecimal lendedAmount = this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(1001L, TransactionType.EXPENSE);
        BigDecimal receivedAmount = this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(1002L, TransactionType.INCOME);

        assertEquals(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_DOWN),
                lendedAmount.setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_DOWN),
                receivedAmount.setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testCreate_amountBiggerThanBalance_shouldThrowException() {
        CreateDebtDto dto = new CreateDebtDto();

        dto.setDebtAmount(BigDecimal.valueOf(500));
        dto.setLenderBudgetId(1001L);
        dto.setReceiverBudgetId(1002L);

        AssertUtil.assertApiException(ApiErrorCode.ERR_0014, () -> this.debtService.create(dto));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testPay_validPayload_shouldPayDebt() {

        Debt debt = this.debtService.findById(101L);
        BudgetStatistics lenderBudgetStatistics = this.budgetService.getStatistics(
                debt.getLenderBudgetId());
        BudgetStatistics receiverBudgetStatistics = this.budgetService.getStatistics(
                debt.getReceiverBudgetId());

        //Test before pay
        assertEquals(BigDecimal.valueOf(302.09),
                debt.getAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(243.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(320.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        PayDebtDto dto = new PayDebtDto();
        dto.setAmount(BigDecimal.valueOf(100));
        debt = this.debtService.pay(101L, dto);

        lenderBudgetStatistics = this.budgetService.getStatistics(debt.getLenderBudgetId());
        receiverBudgetStatistics = this.budgetService.getStatistics(debt.getReceiverBudgetId());

        //Test after pay
        assertEquals(BigDecimal.valueOf(202.09),
                debt.getAmount().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(343.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(220.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testPay_biggerDtoAmount_shouldPayDebt() {

        Debt debt = this.debtService.findById(101L);
        BudgetStatistics lenderBudgetStatistics = this.budgetService.getStatistics(
                debt.getLenderBudgetId());
        BudgetStatistics receiverBudgetStatistics = this.budgetService.getStatistics(
                debt.getReceiverBudgetId());

        //Test before pay
        assertEquals(BigDecimal.valueOf(302.09),
                debt.getAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(243.00).setScale(2, RoundingMode.HALF_DOWN),
                lenderBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        assertEquals(BigDecimal.valueOf(320.00).setScale(2, RoundingMode.HALF_DOWN),
                receiverBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN));

        PayDebtDto dto = new PayDebtDto();
        dto.setAmount(BigDecimal.valueOf(310));

        debt = this.debtService.pay(101L, dto);

        lenderBudgetStatistics = this.budgetService.getStatistics(debt.getLenderBudgetId());
        receiverBudgetStatistics = this.budgetService.getStatistics(debt.getReceiverBudgetId());

        //Test after pay
        assertEquals(BigDecimal.valueOf(0.00).setScale(2, RoundingMode.HALF_DOWN),
                debt.getAmount().setScale(2, RoundingMode.HALF_DOWN)
        );
        assertEquals(BigDecimal.valueOf(545.09),
                lenderBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN)
        );

        assertEquals(BigDecimal.valueOf(17.91),
                receiverBudgetStatistics.getRealBalance().setScale(2, RoundingMode.HALF_DOWN)
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testPay_validPayload_shouldCreateTransaction() {

        PayDebtDto dto = new PayDebtDto();
        dto.setAmount(BigDecimal.valueOf(310));

        this.debtService.pay(101L, dto);


        BigDecimal lendedAmount = this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(1001L, TransactionType.EXPENSE);
        BigDecimal receivedAmount = this.transactionRepository.sumDebtAmountByBudgetIdAndTypeOfNonRevised(1002L, TransactionType.INCOME);

        assertEquals(BigDecimal.valueOf(302.09), lendedAmount.setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(302.09),
                receivedAmount.setScale(2, RoundingMode.HALF_DOWN));

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testPay_amountBiggerThanBalance_shouldThrowException() {
        CreateTransactionServiceDto dto = new CreateTransactionServiceDto();
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setType(TransactionType.EXPENSE);
        dto.setBudgetId(1001L);

        this.transactionService.create(dto, 1L);

        PayDebtDto payDebtDto = new PayDebtDto();

        payDebtDto.setAmount(BigDecimal.valueOf(302.09));

        AssertUtil.assertApiException(ApiErrorCode.ERR_0014,
                () -> this.debtService.pay(101L, payDebtDto));
    }

    @Test
    public void testFindDebt_existedId_shouldReturnDebt() {
        assertNotNull(this.debtService.findDebt(1001L, 1002L));
    }

    @Test
    public void testFindDebt_notExistedId_shouldReturnNull() {
        assertEquals(Optional.empty(), this.debtService.findDebt(1002L, 1003L));
    }

    @Test
    public void testFindById_existedId_shouldReturnDebt() {
        assertNotNull(this.debtService.findById(101L));
    }

    @Test
    public void testFindById_notExistedId_shouldThrowException() {
        AssertUtil.assertApiException(ApiErrorCode.ERR_0013,
                () -> this.debtService.findById(1001L));
    }

    @Test
    public void testFindDebtIfExist_validId_shouldReturnRTransaction() {
        assertNotNull(this.debtService.findDebtIfExists(1001L, 1L));
    }

    @Test
    public void testFindDebtIfExist_invalidId_shouldReturnOptionalEmpty() {
        assertEquals(Optional.empty(), this.debtService.findDebtIfExists(10001L, 1L));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testIsValid_validId_shouldReturnTrue() {

        assertTrue(this.debtService.isValid(101L, 1001L));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testIsValid_invalidId_shouldReturnFalse() {

        assertFalse(this.debtService.isValid(101L, 10001L));
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testIsValid_withNotPermittedUser_shouldReturnFalse() {

        assertFalse(this.debtService.isValid(101L, 1001L));
    }
}

package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.budget.dto.BudgetSimpleDto;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.TransactionDtoSimple;
import com.momo.savanger.api.transaction.dto.TransferTransactionPair;
import com.momo.savanger.api.transfer.dto.TransferFullDto;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransaction;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionRepository;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionService;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import com.momo.savanger.integration.web.WithLocalMockedUser;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
@Sql("classpath:/sql/transfer/budget-it-data.sql")
@Sql("classpath:/sql/transfer/budgets_participants-it-data.sql")
@Sql("classpath:/sql/transfer/tag-it-data.sql")
@Sql("classpath:/sql/transfer/category-it-data.sql")
@Sql("classpath:/sql/transfer/transaction-it-data.sql")
@Sql("classpath:/sql/transfer/transactions_tags-it-data.sql")
@Sql("classpath:/sql/transfer/revision-it-data.sql")
@Sql("classpath:/sql/transfer/debt-it-data.sql")
@Sql("classpath:/sql/transfer/prepayment-it-data.sql")
@Sql("classpath:/sql/transfer/recurring_transaction-it-data.sql")
@Sql("classpath:/sql/transfer/transfer-it-data.sql")
@Sql("classpath:/sql/transfer/transfer_transactions-it-data.sql")
@Sql(value = "classpath:/sql/transfer/del-transfer_transactions-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-transfer-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-recurring_transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-prepayment-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/transfer/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class TransferTransactionServiceIt {

    @Autowired
    private TransferTransactionService transferTransactionService;

    @Autowired
    private TransferTransactionRepository transferTransactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Test
    @WithLocalMockedUser
    @Transactional
    public void testCreate_valid() {
        final CreateTransferTransactionDto dto = new CreateTransferTransactionDto();

        dto.setTransferId(234L);
        dto.setAmount(BigDecimal.valueOf(320));
        dto.setSourceComment("This is a test");
        dto.setReceiverComment("Simcho");
        dto.setSourceCategoryId(302L);
        dto.setReceiverCategoryId(303L);

        assertEquals(1, transferTransactionRepository.findAll().size());

        TransferTransactionDto transferTransaction = transferTransactionService.create(dto);

        final TransferTransactionPair pair = this.transactionService.getTransferTransactionPair(
                transferTransaction.getTransferTransactionId());

        assertNotNull(pair);
        assertEquals(2, transferTransactionRepository.findAll().size());

        this.transferTransactionService.create(dto);
    }

    @Test
    @WithLocalMockedUser
    @Transactional
    public void testCreate_emptyPayload() {
        final CreateTransferTransactionDto dto = new CreateTransferTransactionDto();

        assertThrows(DataIntegrityViolationException.class,
                () -> this.transferTransactionService.create(dto)
        );
    }

    @Test
    public void testGetTransferTransactionDto_validId() {

        final TransferTransactionDto dto = this.transferTransactionService
                .getTransferTransactionDto(594L);

        final TransferFullDto transfer = dto.getTransfer();
        final TransactionDtoSimple sourceTransaction = dto.getSourceTransaction();
        final TransactionDtoSimple receiverTransaction = dto.getReceiverTransaction();

        assertEquals(594, dto.getTransferTransactionId());

        //Transfer tests
        assertEquals(234L, transfer.getId());
        assertEquals(1002L, transfer.getReceiverBudgetId());
        assertEquals(1001L, transfer.getSourceBudgetId());
        assertEquals(true, transfer.getActive());

        //Test budgetDetail in transfer
        BudgetSimpleDto source = transfer.getSourceBudget();
        BudgetSimpleDto receiver = transfer.getReceiverBudget();

        assertEquals(1001L, source.getId());
        assertEquals("bochko", source.getBudgetName());

        assertEquals(1002L, receiver.getId());
        assertEquals("Knigi", receiver.getBudgetName());

        //Test Transactions
        assertEquals(TransactionType.INCOME, receiverTransaction.getType());
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN)
                , receiverTransaction.getAmount()
        );
        assertEquals(1002, receiverTransaction.getBudgetId());
        assertEquals(303L, receiverTransaction.getCategoryId());

        assertEquals(TransactionType.EXPENSE, sourceTransaction.getType());
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN)
                , sourceTransaction.getAmount()
        );
        assertEquals(1001, sourceTransaction.getBudgetId());
        assertEquals(302L, sourceTransaction.getCategoryId());

    }

    @Test
    public void testGetTransferTransactionDto_invalidId() {

        AssertUtil.assertApiException(ApiErrorCode.ERR_0019,
                () -> this.transferTransactionService.getTransferTransactionDto(999L)
        );
    }

    @Test
    public void testGetTransferTransaction_validId() {

        final TransferTransaction transferTransaction = this.transferTransactionService
                .getTransferTransaction(594L);

        assertNotNull(transferTransaction);
    }

    @Test
    public void testGetTransferTransaction_invalidId() {

        AssertUtil.assertApiException(ApiErrorCode.ERR_0019,
                () -> this.transferTransactionService.getTransferTransactionDto(999L)
        );
    }

    @Test
    public void testRevertTransferTransaction_validId() {

        TransferTransactionPair pair = this.transactionService.getTransferTransactionPair(594L);
        assertNotNull(pair);
        assertEquals(1, transferTransactionRepository.findAll().size());

        this.transferTransactionService.revertTransferTransaction(594L);

        AssertUtil.assertApiException(ApiErrorCode.ERR_0020,
                () -> this.transactionService.getTransferTransactionPair(594L));
        assertEquals(0, transferTransactionRepository.findAll().size());
    }

    @Test
    public void testRevertTransferTransaction_invalidId() {

        assertThrows(ApiException.class,
                () -> this.transferTransactionService.revertTransferTransaction(5774L));
    }
}

package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferRepository;
import com.momo.savanger.api.transfer.TransferService;
import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferSearchQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.integration.web.Constants;
import com.momo.savanger.integration.web.WithLocalMockedUser;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
public class TransferServiceIt {

    @Autowired
    private TransferService transferService;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private BudgetService budgetService;


    @Test
    @Transactional
    public void testCreate_validPayload_shouldCreateTransfer() {
        final CreateTransferDto transferDto = new CreateTransferDto();
        transferDto.setReceiverBudgetId(1001L);
        transferDto.setSourceBudgetId(1002L);

        assertEquals(2, this.transferRepository.findAll().size());

        final Transfer transfer = this.transferService.upsert(transferDto);

        assertEquals(3, this.transferRepository.findAll().size());

        final Transfer findedTransfer = this.transferService
                .findTransfer(1001L, 1002L)
                .orElse(null);

        assertNotNull(findedTransfer);
        assertEquals(transfer.getId(), findedTransfer.getId());
    }

    @Test
    @Transactional
    public void testCreate_emptyPayload_shouldThrowException() {
        final CreateTransferDto transferDto = new CreateTransferDto();
        assertThrows(DataIntegrityViolationException.class,
                () -> this.transferService.upsert(transferDto)
        );
    }

    @Test
    @Transactional
    public void testCreate_receiverBudgetEmpty_shouldThrowException() {
        final CreateTransferDto transferDto = new CreateTransferDto();
        transferDto.setSourceBudgetId(1001L);
        assertThrows(DataIntegrityViolationException.class,
                () -> this.transferService.upsert(transferDto)
        );
    }

    @Test
    @Transactional
    public void testCreate_sourceBudgetEmpty_shouldThrowException() {
        final CreateTransferDto transferDto = new CreateTransferDto();
        transferDto.setReceiverBudgetId(1001L);
        assertThrows(DataIntegrityViolationException.class,
                () -> this.transferService.upsert(transferDto)
        );
    }

    @Test
    @Transactional
    public void testDisable_validId_shouldDisableTransfer() {
        Transfer transfer = this.transferService.getById(234L);

        assertTrue(transfer.getActive());

        this.transferService.disable(234L);

        transfer = this.transferService.getById(234L);

        assertFalse(transfer.getActive());
    }

    @Test
    @Transactional
    public void testDisable_invalidId_shouldThrowException() {

        AssertUtil.assertApiException(ApiErrorCode.ERR_0018,
                () -> this.transferService.disable(10L)
        );
    }

    @Test
    @Transactional
    public void testUpsert_validPayloadWithExistData_shouldMakeTransferActive() {

        this.transferService.disable(234L);

        assertEquals(2, this.transferRepository.findAll().size());

        Transfer transfer = this.transferService.getById(234L);

        assertFalse(transfer.getActive());

        final CreateTransferDto transferDto = new CreateTransferDto();
        transferDto.setReceiverBudgetId(1002L);
        transferDto.setSourceBudgetId(1001L);

        transfer = this.transferService.upsert(transferDto);

        assertEquals(2, this.transferRepository.findAll().size());

        assertTrue(transfer.getActive());

    }

    @Test
    public void testGetById_validId_shouldReturnTransfer() {

        Transfer transfer = this.transferService.getById(234L);
        assertNotNull(transfer);
        assertEquals(234L, transfer.getId());
    }

    @Test
    public void testGetById_invalidId_shouldThrowException() {

        AssertUtil.assertApiException(ApiErrorCode.ERR_0018,
                () -> this.transferService.getById(123L)
        );
    }

    @Test
    public void testFindAndFetchDetails_valid_shouldReturnTransferWithDetailsForBudgets() {

        Transfer transfer = this.transferService.findAndFetchDetails(234L);

        Budget receivedBudget = transfer.getReceiverBudget();
        Budget sourceBudget = transfer.getSourceBudget();

        assertNotNull(receivedBudget);
        assertNotNull(sourceBudget);

        assertEquals("bochko", sourceBudget.getBudgetName());
        assertEquals("Knigi", receivedBudget.getBudgetName());

        assertEquals(1001L, sourceBudget.getId());
        assertEquals(1002L, receivedBudget.getId());
    }

    @Test
    public void testFindAndFetchDetails_invalid_shouldThrowException() {

        AssertUtil.assertApiException(ApiErrorCode.ERR_0018,
                () -> this.transferService.findAndFetchDetails(12L)
        );
    }

    @Test
    public void testFindTransfer_validIds_shouldFindTransfer() {
        Transfer transfer = this.transferService
                .findTransfer(1002L, 1001L)
                .orElse(null);

        assertNotNull(transfer);
        assertEquals(234L, transfer.getId());
    }

    @Test
    public void testFindTransfer_invalidIds_resultShouldBeNull() {

        Transfer transfer = this.transferService
                .findTransfer(1002L, 101L)
                .orElse(null);

        assertNull(transfer);
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void searchTransfer_valid() {

        //Create new transfer

        CreateTransferDto transferDto = new CreateTransferDto();
        transferDto.setReceiverBudgetId(1001L);
        transferDto.setSourceBudgetId(1002L);

        this.transferService.upsert(transferDto);

        //Test

        TransferSearchQuery transferSearchQuery = new TransferSearchQuery();

        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);
        PageQuery pageQuery = new PageQuery(0, 1);

        transferSearchQuery.setSort(sortQuery);
        transferSearchQuery.setPage(pageQuery);
        transferSearchQuery.setActive(true);
        transferSearchQuery.setSourceBudgetId(1001L);
        transferSearchQuery.setReceiverBudgetIds(List.of(1002L));

        Page<Transfer> transfers = this.transferService.searchTransfers(transferSearchQuery);

        assertNotNull(transfers);
        assertEquals(1, transfers.getTotalElements());
        assertEquals(234L, transfers.getContent().getFirst().getId());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void searchTransfer_invalid() {

        //Create new budget

        CreateBudgetDto createBudgetDto = new CreateBudgetDto();
        createBudgetDto.setBudgetName("Kotki");
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(234));
        createBudgetDto.setBalance(BigDecimal.ZERO);
        createBudgetDto.setActive(true);
        createBudgetDto.setDateStarted(LocalDateTime.of(2026, 1, 27, 11, 30));
        createBudgetDto.setRecurringRule("FREQ=YEARLY;INTERVAL=1;BYMONTH=10;BYDAY=1MO");
        createBudgetDto.setAutoRevise(false);

        Budget budget = this.budgetService.create(createBudgetDto, 1L);

        //Create new transfers

        CreateTransferDto transferDto = new CreateTransferDto();
        transferDto.setReceiverBudgetId(1001L);
        transferDto.setSourceBudgetId(1002L);

        final Transfer transfer1 = this.transferService.upsert(transferDto);

        transferDto.setReceiverBudgetId(budget.getId());

        final Transfer transfer2 = this.transferService.upsert(transferDto);

        //Test

        TransferSearchQuery transferSearchQuery = new TransferSearchQuery();

        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);
        PageQuery pageQuery = new PageQuery(0, 1);

        transferSearchQuery.setSort(sortQuery);
        transferSearchQuery.setPage(pageQuery);
        transferSearchQuery.setActive(true);
        transferSearchQuery.setReceiverBudgetIds(List.of(1002L));

        //Test with empty source
        Page<Transfer> transfers = this.transferService.searchTransfers(transferSearchQuery);

        assertNotNull(transfers);
        assertEquals(0, transfers.getTotalElements());

        //Test with empty receiver

        transferSearchQuery.setReceiverBudgetIds(null);
        transferSearchQuery.setSourceBudgetId(1002L);

        transfers = this.transferService.searchTransfers(transferSearchQuery);

        assertEquals(2, transfers.getTotalElements());

        this.transferService.disable(transfer2.getId());

        //Test with active true

        transferSearchQuery.setActive(true);

        transfers = this.transferService.searchTransfers(transferSearchQuery);
        assertEquals(1, transfers.getTotalElements());
        assertEquals(transfer1.getId(), transfers.getContent().getFirst().getId());

    }


}

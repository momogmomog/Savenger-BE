package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.transfer.Transfer;
import com.momo.savanger.api.transfer.TransferRepository;
import com.momo.savanger.api.transfer.dto.CreateTransferDto;
import com.momo.savanger.api.transfer.dto.TransferSearchQuery;
import com.momo.savanger.api.transfer.transferTransaction.CreateTransferTransactionDto;
import com.momo.savanger.api.transfer.transferTransaction.TransferTransactionRepository;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
public class TransferControllerIt extends BaseControllerIt {

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TransferTransactionRepository transferTransactionRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validDto_shouldCreateTransfer() throws Exception {
        assertEquals(1, this.transferRepository.findAll().size());

        final CreateTransferDto dto = new CreateTransferDto();
        dto.setReceiverBudgetId(1001L);
        dto.setSourceBudgetId(1002L);

        super.putOK(Endpoints.TRANSFERS, dto);

        assertEquals(2, this.transferRepository.findAll().size());

        //If try to create transfer with same budgets as receiver and source

        super.putOK(Endpoints.TRANSFERS,
                dto
        );

        assertEquals(2, this.transferRepository.findAll().size());

        // If transfer is not active

        super.deleteOK("/transfers/1", null);

        final Transfer transfer = this.transferRepository.findById(1L).orElse(null);

        assertNotNull(transfer);
        assertEquals(1L, transfer.getId());
        assertEquals(false, transfer.getActive());

        super.putOK(Endpoints.TRANSFERS,
                dto,
                jsonPath("$.id", is(1)),
                jsonPath("$.active", is(true))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload() throws Exception {

        //Empty create dto

        final CreateTransferDto transferDto = new CreateTransferDto();

        super.put(Endpoints.TRANSFERS,
                transferDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceBudgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"receiverBudgetId\" && @.constraintName == \"NotNull\")]").exists()

        );

        // not owner or partcipant

        transferDto.setSourceBudgetId(1003L);

        super.put(Endpoints.TRANSFERS,
                transferDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceBudgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

        // invalid id

        transferDto.setSourceBudgetId(100L);

        super.put(Endpoints.TRANSFERS,
                transferDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceBudgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );


    }

    @Test
    @WithLocalMockedUser
    public void testDelete_validId_shouldMakeTransferNotActive() throws Exception {

        super.deleteOK("/transfers/234", null);

        final Transfer transfer = this.transferRepository.findById(234L).orElse(null);

        assertNotNull(transfer);
        assertEquals(234L, transfer.getId());
        assertEquals(false, transfer.getActive());
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testDelete_invalidOwner() throws Exception {

        super.delete("/transfers/234",
                null,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"transferId\" && @.constraintName == \"CanAccessTransfer\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testDelete_invalidId() throws Exception {

        super.delete("/transfers/24",
                null,
                HttpStatus.NOT_FOUND
        );
    }

    @Test
    @WithLocalMockedUser
    public void testSearch_validPayload_shouldReturnTransfer() throws Exception {
        TransferSearchQuery query = new TransferSearchQuery();

        PageQuery pageQuery = new PageQuery(0, 1);

        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);

        query.setSort(sortQuery);
        query.setPage(pageQuery);
        query.setActive(true);
        query.setSourceBudgetId(1001L);
        query.setReceiverBudgetIds(List.of(1002L));

        super.postOK(Endpoints.TRANSFERS_SEARCH,
                query,
                jsonPath("$.page.totalElements", is(1))
        );
    }

    @Test
    @WithLocalMockedUser
    public void testSearch_invalidPayload() throws Exception {

        TransferSearchQuery query = new TransferSearchQuery();

        super.post(Endpoints.TRANSFERS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"page\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sort\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceBudgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

        PageQuery pageQuery = new PageQuery(-1, 0);

        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);

        query.setPage(pageQuery);
        query.setSort(sortQuery);
        query.setSourceBudgetId(1007L);

        super.post(Endpoints.TRANSFERS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"page.pageNumber\" && @.constraintName == \"Min\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"page.pageSize\" && @.constraintName == \"Min\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceBudgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

        pageQuery.setPageNumber(0);
        pageQuery.setPageSize(1);
        query.setSourceBudgetId(1001L);

        sortQuery = new SortQuery();

        query.setSort(sortQuery);

        super.post(Endpoints.TRANSFERS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sort.field\" && @.constraintName == \"NotEmpty\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sort.direction\" && @.constraintName == \"NotNull\")]").exists()
        );

        sortQuery.setDirection(SortDirection.ASC);
        sortQuery.setField("id");
        query.setSort(sortQuery);

        // receiver budget is not owned by this user
        query.setReceiverBudgetIds(List.of(1003L));
        super.postOK(Endpoints.TRANSFERS_SEARCH,
                query,
                jsonPath("$.page.totalElements", is(0))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreateTransferTransaction_validDto_shouldCreateTransfer() throws Exception {

        assertEquals(1, this.transferTransactionRepository.findAll().size());

        final CreateTransferTransactionDto dto = new CreateTransferTransactionDto();
        dto.setTransferId(234L);
        dto.setAmount(BigDecimal.TEN);
        dto.setReceiverComment("fcsd");
        dto.setSourceComment("fddfg");
        dto.setSourceCategoryId(302L);
        dto.setReceiverCategoryId(303L);

        super.postOK(Endpoints.TRANSFER_TRANSACTIONS, dto);

        assertEquals(2, this.transferTransactionRepository.findAll().size());

    }

    @Test
    @WithLocalMockedUser
    public void testCreateTransferTransaction_invalidPayload() throws Exception {

        final CreateTransferTransactionDto dto = new CreateTransferTransactionDto();

        super.post(Endpoints.TRANSFER_TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(4)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"transferId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceCategoryId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"receiverCategoryId\" && @.constraintName == \"NotNull\")]").exists()
        );

        dto.setTransferId(234L);
        dto.setAmount(BigDecimal.valueOf(-98));

        super.post(Endpoints.TRANSFER_TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceCategoryId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"receiverCategoryId\" && @.constraintName == \"NotNull\")]").exists()

        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_invalidOwner() throws Exception {

        final CreateTransferTransactionDto dto = new CreateTransferTransactionDto();
        dto.setTransferId(234L);
        dto.setAmount(BigDecimal.TEN);
        dto.setReceiverComment("fcsd");
        dto.setSourceComment("fddfg");
        dto.setSourceCategoryId(302L);
        dto.setReceiverCategoryId(304L);

        super.post(Endpoints.TRANSFER_TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"transferId\""
                                + " && @.constraintName == \"CanAccessTransfer\")]").exists()
        );

        dto.setTransferId(235L);
        dto.setReceiverCategoryId(302L);
        dto.setSourceCategoryId(304L);

        super.post(Endpoints.TRANSFER_TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sourceCategoryId\""
                                + " && @.constraintName == \"ValidCreateTransferTransactionDto\")]").exists()

        );


    }

    @Test
    @WithLocalMockedUser
    public void testGetTransferTransaction_validId_shouldReturnTransfer() throws Exception {

        super.getOK("/transfers/transactions/594");
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testGetTransferTransaction_invalidOwner() throws Exception {

        super.get("/transfers/transactions/594",
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"transferTransactionId\" && @.constraintName == \"CanAccessTransferTransaction\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser
    public void testGetTransferTransaction_invalidId() throws Exception {

        super.get("/transfers/transactions/94",
                HttpStatus.NOT_FOUND);
    }


    @Test
    @WithLocalMockedUser
    public void testDeleteTransferTransaction_validId_shouldReturnTransfer() throws Exception {

        super.getOK("/transfers/transactions/594");
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testDeleteTransferTransaction_invalidOwner() throws Exception {

        super.get("/transfers/transactions/594",
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"transferTransactionId\" && @.constraintName == \"CanAccessTransferTransaction\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser
    public void testDeleteTransferTransaction_invalidId() throws Exception {

        super.get("/transfers/transactions/94",
                HttpStatus.NOT_FOUND);
    }


}

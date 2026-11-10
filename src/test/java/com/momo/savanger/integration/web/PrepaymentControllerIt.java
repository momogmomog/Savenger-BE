package com.momo.savanger.integration.web;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentQuery;
import com.momo.savanger.api.prepayment.PrepaymentRepository;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
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
public class PrepaymentControllerIt extends BaseControllerIt {

    @Autowired
    private PrepaymentRepository prepaymentRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreatePrepayment() throws Exception {
        assertEquals(2, this.prepaymentRepository.findAll().size());

        CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setRecurringTransactionId(9001L);
        dto.setAmount(BigDecimal.valueOf(10));
        dto.setBudgetId(1001L);
        dto.setName("PArno");
        dto.setPaidUntil(LocalDateTime.now().plusMonths(4));

        super.postOK(Endpoints.PREPAYMENTS,
                dto);

        assertEquals(3, this.prepaymentRepository.findAll().size());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload() throws Exception {

        CreatePrepaymentDto prepaymentDto = new CreatePrepaymentDto();

        //Test empty dto
        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(5)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"name\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"paidUntil\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransactionId\" && @.constraintName == \"OneMustNotBeNull\")]").exists()
        );

        CreateRecurringTransactionDto transactionDto = new CreateRecurringTransactionDto();
        transactionDto.setAmount(BigDecimal.valueOf(40));
        transactionDto.setType(TransactionType.EXPENSE);
        transactionDto.setCategoryId(1001L);
        transactionDto.setAutoExecute(false);
        transactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        transactionDto.setBudgetId(1001L);
        transactionDto.setIncludeInBalance(false);

        //Test minus amount
        prepaymentDto.setName("Kola");
        prepaymentDto.setPaidUntil(LocalDateTime.now().plusMonths(6));
        prepaymentDto.setBudgetId(1001L);
        prepaymentDto.setAmount(BigDecimal.valueOf(-300));
        prepaymentDto.setRecurringTransaction(transactionDto);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"MinValueZero\")]").exists()
        );

        //Test one of rTransaction fields should be null
        prepaymentDto.setAmount(BigDecimal.valueOf(10));
        prepaymentDto.setRecurringTransactionId(9001L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransactionId\""
                                + " && @.constraintName == \"OneMustBeNull\")]").exists()
        );

        //Test recurring transaction budgetId equals to prepayment budgetId

        prepaymentDto.setBudgetId(1002L);
        prepaymentDto.setRecurringTransactionId(null);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"recurringTransaction.budgetId\""
                        + "&& @.constraintName == \"ValidPrepaymentDto\")]").exists()
        );

        //Test amount is bigger than budget balance
        prepaymentDto.setBudgetId(1001L);
        prepaymentDto.setAmount(BigDecimal.valueOf(10000));

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"amount\""
                        + "&& @.constraintName == \"IsBudgetBalanceBigger\")]").exists()
        );

        //Test recurring transaction exist
        prepaymentDto.setAmount(BigDecimal.valueOf(20));
        prepaymentDto.setRecurringTransactionId(15L);
        prepaymentDto.setRecurringTransaction(null);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"recurringTransactionId\""
                        + "&& @.constraintName == \"ValidPrepaymentDto\")]").exists()
        );

        //Test user has access to budget

        prepaymentDto.setRecurringTransactionId(9001L);
        prepaymentDto.setBudgetId(1003L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath("fieldErrors.[?(@.field == \"budgetId\""
                        + "&& @.constraintName == \"CanAccessBudget\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"recurringTransactionId\""
                        + "&& @.constraintName == \"ValidPrepaymentDto\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetPrepayment_validId_shouldReturnPrepayment() throws Exception {
        super.getOK("/prepayments/1001",
                jsonPath("id", is(1001)),
                jsonPath("name", is("netsurf")),
                jsonPath("amount", is(200.0)),
                jsonPath("remainingAmount", is(200.0)),
                jsonPath("completed", is(false)),
                jsonPath("budgetId", is(1001))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetPrepayment_completedPrepayment_shouldReturnPrepayment() throws Exception {
        super.postOK("/prepayments/pay/9001", null);
        super.postOK("/prepayments/pay/9001", null);

        super.getOK("/prepayments/1001",
                jsonPath("id", is(1001)),
                jsonPath("completed", is(true)),
                jsonPath("remainingAmount", is(0.0))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetPrepayment_invalidId_shouldThrowException() throws Exception {
        super.get("/prepayments/99999",
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"id\" "
                        + "&& @.constraintName == \"ValidPrepayment\" "
                        + "&& @.message == \"Prepayment does not exist.\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testGetPrepayment_invalidUser_shouldThrowException() throws Exception {
        super.get("/prepayments/1001",
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"id\" "
                        + "&& @.constraintName == \"ValidPrepayment\" "
                        + "&& @.message == \"Access denied\")]").exists()
        );
    }

    @Test
    @Transactional
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testEditPrepayment_validPayload_shouldUpdatePrepayment() throws Exception {
        // Budget 1001 has realBalance = 422.09 (balance 321 - expenses 1 + debtReceived 302.09 - prepayment 200).
        // Editing prepayment 1001 adds back its existing remainingAmount (200), giving availableBalance = 622.09.
        // Using amount = 500 (> 422.09 and <= 622.09) verifies getExistingPrepaymentIfEdit() adds back remainingAmount.
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setRecurringTransactionId(9001L);
        dto.setAmount(BigDecimal.valueOf(500));
        dto.setBudgetId(1001L);
        dto.setName("Updated netsurf");
        dto.setPaidUntil(LocalDateTime.now().plusMonths(8));

        super.putOK("/prepayments/1001",
                dto,
                jsonPath("id", is(1001)),
                jsonPath("name", is("Updated netsurf")),
                jsonPath("amount", is(500)),
                jsonPath("remainingAmount", is(500)),
                jsonPath("completed", is(false)),
                jsonPath("budgetId", is(1001))
        );

        final var updated = this.prepaymentRepository.findById(1001L).orElseThrow();
        assertEquals("Updated netsurf", updated.getName());
        assertEquals(BigDecimal.valueOf(500).setScale(2, RoundingMode.HALF_DOWN),
                updated.getAmount().setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testEditPrepayment_amountExceedsAvailableBalanceWithExistingRemainingAmount_shouldFail()
            throws Exception {
        // Budget 1001 has realBalance = 422.09.
        // On create (POST), amount = 500 fails because 500 > 422.09.
        // On edit (PUT /prepayments/1001), availableBalance is 422.09 + 200.00 = 622.09, so 623 fails.
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setRecurringTransactionId(9001L);
        dto.setAmount(BigDecimal.valueOf(500));
        dto.setBudgetId(1001L);
        dto.setName("Updated netsurf");
        dto.setPaidUntil(LocalDateTime.now().plusMonths(8));

        super.post(Endpoints.PREPAYMENTS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"amount\" "
                        + "&& @.constraintName == \"IsBudgetBalanceBigger\")]").exists()
        );

        dto.setAmount(BigDecimal.valueOf(623));

        super.put("/prepayments/1001",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"amount\" "
                        + "&& @.constraintName == \"IsBudgetBalanceBigger\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testEditPrepayment_invalidIdAndCompleted_shouldThrowException() throws Exception {
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setRecurringTransactionId(9001L);
        dto.setAmount(BigDecimal.valueOf(150));
        dto.setBudgetId(1001L);
        dto.setName("Updated netsurf");
        dto.setPaidUntil(LocalDateTime.now().plusMonths(8));

        // Non-existent ID
        super.put("/prepayments/99999",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"id\" "
                        + "&& @.constraintName == \"ValidPrepayment\" "
                        + "&& @.message == \"Prepayment does not exist.\")]").exists()
        );

        // Complete prepayment 1001 and verify edit is rejected
        super.postOK("/prepayments/pay/9001", null);
        super.postOK("/prepayments/pay/9001", null);

        final CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();
        rTransactionDto.setAmount(BigDecimal.valueOf(40));
        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setCategoryId(1001L);
        rTransactionDto.setAutoExecute(false);
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setBudgetId(1001L);
        rTransactionDto.setIncludeInBalance(false);

        dto.setRecurringTransactionId(null);
        dto.setRecurringTransaction(rTransactionDto);

        super.put("/prepayments/1001",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"id\" "
                        + "&& @.constraintName == \"ValidPrepayment\" "
                        + "&& @.message == \"Prepayment does not exist.\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testEditPrepayment_invalidUser_shouldThrowException() throws Exception {
        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setRecurringTransactionId(9001L);
        dto.setAmount(BigDecimal.valueOf(150));
        dto.setBudgetId(1001L);
        dto.setName("Updated netsurf");
        dto.setPaidUntil(LocalDateTime.now().plusMonths(8));

        super.put("/prepayments/1002",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"id\" "
                        + "&& @.constraintName == \"ValidPrepayment\" "
                        + "&& @.message == \"Access denied\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testEditPrepayment_differentBudgetId_shouldThrowException() throws Exception {
        final CreateRecurringTransactionDto rTransactionDto = new CreateRecurringTransactionDto();
        rTransactionDto.setAmount(BigDecimal.valueOf(40));
        rTransactionDto.setType(TransactionType.EXPENSE);
        rTransactionDto.setCategoryId(1002L);
        rTransactionDto.setAutoExecute(false);
        rTransactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        rTransactionDto.setBudgetId(1002L);
        rTransactionDto.setIncludeInBalance(false);

        final CreatePrepaymentDto dto = new CreatePrepaymentDto();
        dto.setRecurringTransaction(rTransactionDto);
        dto.setAmount(BigDecimal.valueOf(100));
        dto.setBudgetId(1002L);
        dto.setName("Updated netsurf");
        dto.setPaidUntil(LocalDateTime.now().plusMonths(8));

        super.put("/prepayments/1001",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("$.errorCode", is("ERR_0024"))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testSearchPrepayments_validPayload_shouldReturnPrepayments() throws Exception {
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

        super.postOK(Endpoints.PREPAYMENTS_SEARCH,
                query,
                jsonPath("$.content.length()", is(1)),
                jsonPath("$.content[0].id", is(1001)),
                jsonPath("$.content[0].name", is("netsurf")),
                jsonPath("$.content[0].amount", is(200.0)),
                jsonPath("$.content[0].remainingAmount", is(200.0)),
                jsonPath("$.content[0].budgetId", is(1001))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testSearchPrepayments_invalidPayload_shouldThrowException() throws Exception {
        final PrepaymentQuery query = new PrepaymentQuery();

        super.post(Endpoints.PREPAYMENTS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath("fieldErrors.[?(@.field == \"page\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"sort\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

        query.setPage(new PageQuery(-1, 0));
        query.setSort(new SortQuery("id", SortDirection.ASC));
        query.setBudgetId(1003L);

        super.post(Endpoints.PREPAYMENTS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath("fieldErrors.[?(@.field == \"page.pageNumber\" && @.constraintName == \"Min\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"page.pageSize\" && @.constraintName == \"Min\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );
    }


}

package com.momo.savanger.integration.web;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.prepayment.CreatePrepaymentDto;
import com.momo.savanger.api.prepayment.PrepaymentRepository;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.recurring.CreateRecurringTransactionDto;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
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
        dto.setRecurringTransactionId(1001L);
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
                        "fieldErrors.[?(@.field == \"recurringTransactionId\" && @.constraintName == \"OneOfTheseNotBeNull\")]").exists()
        );

        CreateRecurringTransactionDto transactionDto = new CreateRecurringTransactionDto();
        transactionDto.setAmount(BigDecimal.valueOf(40));
        transactionDto.setType(TransactionType.EXPENSE);
        transactionDto.setCategoryId(1001L);
        transactionDto.setAutoExecute(false);
        transactionDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        transactionDto.setBudgetId(1001L);

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
        prepaymentDto.setRecurringTransactionId(1001L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringTransactionId\""
                                + " && @.constraintName == \"OneMustBeNull\")]").exists()
        );

        //Test budget ids equals

        prepaymentDto.setBudgetId(1002L);
        prepaymentDto.setRecurringTransactionId(null);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"recurringTransaction.budgetId\""
                        + "&& @.constraintName == \"BudgetsShouldBeEquals\")]").exists()
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
                        + "&& @.constraintName == \"RecurringTransactionExist\")]").exists()
        );

        //Test user has access to budget

        prepaymentDto.setRecurringTransactionId(1001L);
        prepaymentDto.setBudgetId(1003L);

        super.post(Endpoints.PREPAYMENTS,
                prepaymentDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath("fieldErrors.[?(@.field == \"budgetId\""
                        + "&& @.constraintName == \"CanAccessBudget\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"recurringTransaction.budgetId\""
                        + "&& @.constraintName == \"BudgetsShouldBeEquals\")]").exists()
        );
    }


}

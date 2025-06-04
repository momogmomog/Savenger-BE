package com.momo.savanger.integration.web;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/tag-it-data.sql")
@Sql("classpath:/sql/category-it-data.sql")
@Sql("classpath:/sql/transaction-it-data.sql")
@Sql("classpath:/sql/transactions_tags-it-data.sql")
@Sql(value = "classpath:/sql/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class TransactionControllerIt extends BaseControllerIt {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldSaveTransaction() throws Exception {

        assertEquals(3, this.transactionRepository.findAll().size());

        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(43.33));
        dto.setBudgetId(1001L);
        dto.setCategoryId(1001L);

        List<Long> ids = new ArrayList<>();
        ids.add(1001L);

        dto.setTagIds(ids);
        super.postOK(Endpoints.TRANSACTIONS, dto);

        assertEquals(4, this.transactionRepository.findAll().size());

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload() throws Exception {

        CreateTransactionDto dto = new CreateTransactionDto();

        super.post(Endpoints.TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(4)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"type\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists()

        );

        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(-43.33));
        dto.setCategoryId(1001L);
        dto.setBudgetId(1001L);

        super.post(Endpoints.TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"MinValueZero\")]").exists()
        );

        dto.setAmount(BigDecimal.valueOf(0));
        dto.setBudgetId(1006L);

        super.post(Endpoints.TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryId\" && @.constraintName == \"ValidTransactionDto\")]").exists()
        );

        dto.setBudgetId(1001L);
        dto.setCategoryId(1003L);

        super.post(Endpoints.TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"categoryId\" "
                        + "&& @.constraintName == \"ValidTransactionDto\" "
                        + "&& @.message == \"Category does not exist or budget is not valid.\")]").exists()
        );

        dto.setCategoryId(1001L);

        List<Long> ids = new ArrayList<>();
        ids.add(1001L);
        ids.add(1003L);

        dto.setTagIds(ids);

        super.post(Endpoints.TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"tagIds\" "
                        + "&& @.constraintName == \"ValidTransactionDto\" "
                        + "&& @.message == \"Invalid tags: [1003]\")]").exists()
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testSearchTransactions_validPayload_shouldReturnTransactions() throws Exception {

        TransactionSearchQuery query = new TransactionSearchQuery();

        PageQuery pageQuery = new PageQuery(0, 1);

        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);

        BetweenQuery<BigDecimal> amount = new BetweenQuery<>(BigDecimal.valueOf(0),
                BigDecimal.valueOf(1000));

        query.setSort(sortQuery);
        query.setPage(pageQuery);
        query.setAmount(amount);
        query.setCategoryId(1001L);
        query.setBudgetId(1001L);
        query.setUserId(1L);
        query.setType(TransactionType.INCOME);

        super.postOK(Endpoints.TRANSACTIONS_SEARCH, query);
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testSearchTransactions_invalidPayload_shouldThrowExceptions() throws Exception {

        TransactionSearchQuery query = new TransactionSearchQuery();

        super.post(Endpoints.TRANSACTIONS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"page\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sort\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

        PageQuery pageQuery = new PageQuery(-1, 0);

        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);

        query.setPage(pageQuery);
        query.setSort(sortQuery);
        query.setBudgetId(1007L);

        super.post(Endpoints.TRANSACTIONS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"page.pageNumber\" && @.constraintName == \"Min\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"page.pageSize\" && @.constraintName == \"Min\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

        pageQuery.setPageNumber(0);
        pageQuery.setPageSize(1);
        query.setBudgetId(1001L);

        sortQuery = new SortQuery();

        query.setSort(sortQuery);

        super.post(Endpoints.TRANSACTIONS_SEARCH,
                query,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sort.field\" && @.constraintName == \"NotEmpty\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"sort.direction\" && @.constraintName == \"NotNull\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testEdit_invalidPayload() throws Exception {

        EditTransactionDto dto = new EditTransactionDto();

        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(-43.33));
        dto.setCategoryId(1001L);
        dto.setBudgetId(1001L);

        super.post("/transactions/1001/edit",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"MinValueZero\")]").exists()
        );

        dto.setAmount(BigDecimal.ZERO);
        dto.setBudgetId(1003L);
        super.post("/transactions/1001/edit",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"categoryId\" && @.constraintName == \"ValidTransactionDto\")]").exists()

        );

        dto.setBudgetId(1001L);

        List<Long> ids = new ArrayList<>();
        ids.add(1001L);
        ids.add(1003L);

        dto.setTagIds(ids);

        super.post("/transactions/1001/edit",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath("fieldErrors.[?(@.field == \"tagIds\" "
                        + "&& @.constraintName == \"ValidTransactionDto\" "
                        + "&& @.message == \"Invalid tags: [1003]\")]").exists()
        );

        ids.remove(1003L);
        dto.setTagIds(ids);

        super.post("/transactions/1003/edit",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"id\" && @.constraintName == \"TransactionRevised\")]").exists()

        );


    }
}

package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.CreateTransactionServiceDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQueryForAnalytics;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.util.List;
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
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/tag-it-data.sql")
@Sql("classpath:/sql/category-it-data.sql")
@Sql("classpath:/sql/transaction-it-data.sql")
@Sql("classpath:/sql/transactions_tags-it-data.sql")
@Sql("classpath:/sql/budgets_participants-it-data.sql")
@Sql(value = "classpath:/sql/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class AnalyticsControllerIt extends BaseControllerIt {

    @Autowired
    private TransactionService transactionService;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetCategoriesSpendingBreakdown_validQuery_shouldReturnSummaryAndItems()
            throws Exception {
        final TransactionSearchQueryForAnalytics query = new TransactionSearchQueryForAnalytics();
        query.setBudgetId(1001L);
        query.setRevised(false);

        super.postOK(
                Endpoints.ANALYTICS_CATEGORIES,
                query,
                jsonPath("$.incomesSum", is(0)),
                jsonPath("$.expensesSum", is(45.0)),
                jsonPath("$.total", is(-45.0)),
                jsonPath("$.incomesCount", is(0)),
                jsonPath("$.expensesCount", is(1)),
                jsonPath("$.totalCount", is(1)),
                jsonPath("$.items", hasSize(1)),
                jsonPath("$.items[0].category.id", is(1001)),
                jsonPath("$.items[0].incomes.sum", is(0)),
                jsonPath("$.items[0].incomes.count", is(0)),
                jsonPath("$.items[0].incomes.transactions", hasSize(0)),
                jsonPath("$.items[0].expenses.sum", is(45.0)),
                jsonPath("$.items[0].expenses.count", is(1)),
                jsonPath("$.items[0].expenses.transactions", hasSize(1)),
                jsonPath("$.items[0].expenses.transactions[0].id", is(1004)),
                jsonPath("$.items[0].expenses.transactions[0].amount", is(45.0)),
                jsonPath("$.items[0].expenses.transactions[0].type", is("EXPENSE"))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetTagsSpendingBreakdown_multiTagTransactions_shouldDeduplicateInSummary()
            throws Exception {
        final CreateTransactionServiceDto expenseMultiTag = new CreateTransactionServiceDto();
        expenseMultiTag.setType(TransactionType.EXPENSE);
        expenseMultiTag.setAmount(BigDecimal.valueOf(30.00));
        expenseMultiTag.setBudgetId(1001L);
        expenseMultiTag.setCategoryId(1001L);
        expenseMultiTag.setTagIds(List.of(1001L, 1003L));
        this.transactionService.create(expenseMultiTag, 1L);

        final CreateTransactionServiceDto incomeSingleTag = new CreateTransactionServiceDto();
        incomeSingleTag.setType(TransactionType.INCOME);
        incomeSingleTag.setAmount(BigDecimal.valueOf(100.00));
        incomeSingleTag.setBudgetId(1001L);
        incomeSingleTag.setCategoryId(1001L);
        incomeSingleTag.setTagIds(List.of(1001L));
        this.transactionService.create(incomeSingleTag, 1L);

        final TransactionSearchQueryForAnalytics query = new TransactionSearchQueryForAnalytics();
        query.setBudgetId(1001L);
        query.setRevised(false);

        super.postOK(
                Endpoints.ANALYTICS_TAGS,
                query,
                jsonPath("$.incomesSum", is(100.0)),
                jsonPath("$.expensesSum", is(30.0)),
                jsonPath("$.total", is(70.0)),
                jsonPath("$.incomesCount", is(1)),
                jsonPath("$.expensesCount", is(1)),
                jsonPath("$.totalCount", is(2)),
                jsonPath("$.items", hasSize(2))
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testGetCategoriesSpendingBreakdown_noMatchingTransactions_shouldReturnEmptySummary()
            throws Exception {
        final TransactionSearchQueryForAnalytics query = new TransactionSearchQueryForAnalytics();
        query.setBudgetId(1001L);
        query.setRevised(false);
        query.setComment("non-existent-comment");

        super.postOK(
                Endpoints.ANALYTICS_CATEGORIES,
                query,
                jsonPath("$.incomesSum", is(0)),
                jsonPath("$.expensesSum", is(0)),
                jsonPath("$.total", is(0)),
                jsonPath("$.incomesCount", is(0)),
                jsonPath("$.expensesCount", is(0)),
                jsonPath("$.totalCount", is(0)),
                jsonPath("$.items", hasSize(0))
        );
    }
}

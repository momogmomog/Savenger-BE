package com.momo.savanger.integration.api;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.Transaction;
import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
public class TransactionServiceIt {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    UserService userService;

    @Test
    @Transactional
    public void testCreateTransaction_validPayload_shouldSaveTransaction() {

        assertEquals(3, this.transactionRepository.findAll().size());

        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(43.33));
        dto.setBudgetId(1001L);
        dto.setCategoryId(1001L);

        List<Long> ids = new ArrayList<>();
        ids.add(1001L);
        ids.add(1002L);

        dto.setTagIds(ids);

        User user = this.userService.getById(1L);

        this.transactionService.create(dto, user);

        assertEquals(4, this.transactionRepository.findAll().size());

        Transaction transaction = this.transactionService.findById(1L);

        assertNotNull(transaction);

        assertEquals("EXPENSE", transaction.getType().toString());
        assertEquals(BigDecimal.valueOf(43.33), transaction.getAmount());
        assertEquals(1001L, transaction.getBudgetId());
        assertEquals(1001L, transaction.getCategoryId());
        assertEquals(2, transaction.getTags().size());
        assertEquals(user.getId(), transaction.getUserId());
        assertFalse(transaction.getRevised());
    }

    @Test
    @Transactional
    public void testCreateTransaction_emptyPayload_shouldThrowException() {

        CreateTransactionDto dto = new CreateTransactionDto();

        User user = this.userService.getById(1L);

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.transactionService.create(dto, user);
        });

    }

    @Test
    public void testFindById_validId_shouldReturnTransaction() {
        Transaction transaction = this.transactionService.findById(1001L);

        assertNotNull(transaction);
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        assertThrows(ApiException.class, () -> this.transactionService.findById(1006L));
    }

    @Test
    @Transactional
    public void testSearchTransactions_validPayload_shouldReturnTransactions() {

        TransactionSearchQuery query = new TransactionSearchQuery();

        PageQuery pageQuery = new PageQuery(0, 3);

        SortQuery sortQuery = new SortQuery("ds", SortDirection.ASC);

        User user = this.userService.getById(1L);

        // Search by budgetId and Type
        query.setSort(sortQuery);
        query.setPage(pageQuery);
        query.setBudgetId(1001L);
        query.setType(TransactionType.INCOME);

        Page<Transaction> transactions = this.transactionService.searchTransactions(query,
                user);

        assertEquals(3, transactions.getTotalElements());

        assertEquals(1001L, transactions.getContent().getFirst().getId());
        assertEquals(1002L, transactions.getContent().get(1).getId());
        assertEquals(1003L, transactions.getContent().get(2).getId());

        //Test page 2
        pageQuery.setPageNumber(2);
        pageQuery.setPageSize(1);

        query.setPage(pageQuery);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1003L, transactions.getContent().getFirst().getId());

        //Test page 0

        pageQuery.setPageNumber(0);
        pageQuery.setPageSize(1);

        query.setPage(pageQuery);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1001L, transactions.getContent().getFirst().getId());

        pageQuery.setPageSize(3);

        // Search by budgetId, Type and Revised
        query.setRevised(true);

        sortQuery = new SortQuery("id", SortDirection.ASC);

        query.setSort(sortQuery);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1, transactions.getTotalElements());
        assertEquals(1003L, transactions.getContent().getFirst().getId());

        query.setRevised(null);

        // Search by budgetId, Type and comment
        query.setComment("Hrana");

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1, transactions.getTotalElements());
        assertEquals(1002L, transactions.getContent().getFirst().getId());

        query.setComment(null);

        BetweenQuery<BigDecimal> amount = new BetweenQuery<>(BigDecimal.valueOf(0),
                BigDecimal.valueOf(500));

        // Search by budgetId, Type and amount
        query.setAmount(amount);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(2, transactions.getTotalElements());
        assertEquals(1001L, transactions.getContent().getFirst().getId());
        assertEquals(1002L, transactions.getContent().get(1).getId());

        // Search by budgetId, Type, amount and tag
        query.setTagId(1001L);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1, transactions.getTotalElements());
        assertEquals(1001L, transactions.getContent().getFirst().getId());

        BetweenQuery<LocalDateTime> dateCreated = new BetweenQuery<>(LocalDateTime.of(2025, 1, 1, 0,
                0, 0), LocalDateTime.now());

        // Search by budgetId, Type, amount and date
        query.setDateCreated(dateCreated);
        query.setTagId(null);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1, transactions.getTotalElements());
        assertEquals(1002L, transactions.getContent().getFirst().getId());

        // Search by budgetId, type, amount, date and categoryId
        query.setCategoryId(1002L);
        amount = new BetweenQuery<>(BigDecimal.ZERO, BigDecimal.valueOf(600));
        query.setAmount(amount);

        transactions = this.transactionService.searchTransactions(query, user);

        assertEquals(1, transactions.getTotalElements());
        assertEquals(1003L, transactions.getContent().getFirst().getId());
    }
}

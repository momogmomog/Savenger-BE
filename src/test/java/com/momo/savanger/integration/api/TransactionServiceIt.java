package com.momo.savanger.integration.api;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.transaction.Transaction;
import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.CreateTransactionDto;
import com.momo.savanger.api.transaction.dto.EditTransactionDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private UserService userService;

    @Autowired
    private BudgetService budgetService;

    @Test
    @Transactional
    public void testCreateTransaction_validPayload_shouldSaveTransaction() {

        assertEquals(4, this.transactionRepository.findAll().size());

        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(43.33));
        dto.setBudgetId(1001L);
        dto.setCategoryId(1001L);

        List<Long> ids = new ArrayList<>();
        ids.add(1001L);

        dto.setTagIds(ids);

        User user = this.userService.getById(1L);

        Transaction transaction = this.transactionService.create(dto, user.getId());

        assertEquals(5, this.transactionRepository.findAll().size());

        assertNotNull(transaction);

        assertEquals("EXPENSE", transaction.getType().toString());
        assertEquals(BigDecimal.valueOf(43.33), transaction.getAmount());
        assertEquals(1001L, transaction.getBudgetId());
        assertEquals(1001L, transaction.getCategoryId());
        assertEquals(1, transaction.getTags().size());
        assertEquals(user.getId(), transaction.getUserId());
        assertFalse(transaction.getRevised());
    }

    @Test
    @Transactional
    public void testCreateTransaction_emptyPayload_shouldThrowException() {

        CreateTransactionDto dto = new CreateTransactionDto();

        User user = this.userService.getById(1L);

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.transactionService.create(dto, user.getId());
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

    @Test
    @Transactional
    public void testEditTransaction_validPayload_shouldEditTransaction() {
        EditTransactionDto dto = new EditTransactionDto();
        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(254.99));
        dto.setComment("Kupih si vafli");
        dto.setCategoryId(1002L);
        dto.setDateCreated(LocalDateTime.now());
        dto.setBudgetId(1002L);
        dto.setTagIds(List.of(1002L));

        Transaction transaction = this.transactionService.edit(1001L, dto);

        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals(BigDecimal.valueOf(254.99), transaction.getAmount());
        assertEquals("Kupih si vafli", transaction.getComment());
        assertEquals(1002L, transaction.getBudgetId());
        assertEquals(1002L, transaction.getCategoryId());
        assertEquals(1, transaction.getTags().size());
        assertThat(List.of(1002L))
                .hasSameElementsAs(
                        transaction.getTags().stream().map(Tag::getId).toList()
                );
    }

    @Test
    @Transactional
    public void testDeleteTransaction_validId_shouldDeleteTransaction() {

        List<Transaction> transactions = this.transactionRepository.findAll();

        assertEquals(4, transactions.size());

        this.transactionService.deleteById(1001L);

        transactions = this.transactionRepository.findAll();

        assertEquals(3, transactions.size());
    }

    @Test
    public void testCanDeleteTransaction() {

        //Test with valid parameters
        User user = this.userService.getById(1L);

        assertTrue(this.transactionService.canDeleteTransaction(1001L, user));

        //Test with revised "true"

        assertFalse(this.transactionService.canDeleteTransaction(1003L, user));

        //Test with invalid id

        assertFalse(this.transactionService.canDeleteTransaction(1006L, user));

        //Test with invalid owner

        user = this.userService.getById(2L);
        assertFalse(this.transactionService.canDeleteTransaction(1002L, user));
    }

    @Test
    public void testIsTransactionValid() {

        //Valid id (owner)
        assertTrue(this.transactionService.canViewTransaction(1001L, 1L));

        //Invalid transaction Id
        assertFalse(this.transactionService.canViewTransaction(10002L, 1L));

        //Valid participant id
        assertFalse(this.transactionService.canViewTransaction(1001L, 3L));
        this.budgetService.addParticipant(new AssignParticipantDto(
                3L,
                this.budgetService.findById(1001L)
        ));
        assertTrue(this.transactionService.canViewTransaction(1001L, 3L));

    }

    @Test
    public void testGetEarningsAmount_validId_shouldReturnEarningsAmount() {
        BigDecimal earningsAmount = this.transactionService.getEarningsAmount(1001L);

        assertEquals(BigDecimal.valueOf(123.32),
                earningsAmount.setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testGetExpensesAmount_validId_shouldReturnExpensesAmount() {
        BigDecimal expensesAmount = this.transactionService.getExpensesAmount(1001L);

        assertEquals(BigDecimal.valueOf(45.00).setScale(2, RoundingMode.HALF_DOWN),
                expensesAmount.setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testGetEarningsAmount_budgetWithoutTransaction_shouldReturnZero() {
        BigDecimal earningsAmount = this.transactionService.getEarningsAmount(1002L);

        assertEquals(BigDecimal.ZERO, earningsAmount);
    }

    @Test
    public void testGetExpensesAmount_budgetWithoutTransaction_shouldReturnZero() {
        BigDecimal expensesAmount = this.transactionService.getEarningsAmount(1002L);

        assertEquals(BigDecimal.ZERO, expensesAmount);
    }

    @Test
    public void testCreateCompensationTransaction_validId_shouldCreateCompensationTransaction() {

        Transaction transaction = this.transactionService.createCompensationTransaction(1001L,
                BigDecimal.valueOf(32.22));

        assertEquals(TransactionType.COMPENSATE, transaction.getType());
        assertEquals(BigDecimal.valueOf(32.22),
                transaction.getAmount().setScale(2, RoundingMode.HALF_DOWN));
    }


}

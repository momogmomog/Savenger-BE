package com.momo.savanger.integration.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetRepository;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.AssignParticipantDto;
import com.momo.savanger.api.budget.dto.BudgetSearchQuery;
import com.momo.savanger.api.budget.dto.BudgetStatistics;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.budget.dto.UnassignParticipantDto;
import com.momo.savanger.api.budget.dto.UpdateBudgetDto;
import com.momo.savanger.api.revision.Revision;
import com.momo.savanger.api.revision.RevisionService;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserRepository;
import com.momo.savanger.api.user.UserService;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import com.momo.savanger.util.AssertUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/budgets_participants-it-data.sql")
@Sql("classpath:/sql/category-it-data.sql")
@Sql("classpath:/sql/transaction-it-data.sql")
@Sql("classpath:/sql/revision-it-data.sql")
@Sql(value = "classpath:/sql/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetServiceIt {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RevisionService revisionService;

    @Test
    public void testCreate_validPayload_shouldCreate() {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();
        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.of(2025, 11, 7, 15, 51));
        createBudgetDto.setBalance(BigDecimal.valueOf(243.4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);

        Budget budget = this.budgetService.create(createBudgetDto, 1L);

        assertNotNull(budget);

        List<Budget> budgets = this.budgetRepository.findAll();

        assertEquals(5, budgets.size());

        assertThat(List.of("Food", "sdf", "Food", "Home", "Test"))
                .hasSameElementsAs(
                        budgets.stream().map(Budget::getBudgetName).toList()
                );

        assertEquals("Test", budget.getBudgetName());
        assertEquals("FREQ=DAILY;INTERVAL=1", budget.getRecurringRule());
        assertEquals(BigDecimal.valueOf(243.4).setScale(2, RoundingMode.HALF_DOWN),
                budget.getBalance());
        assertEquals(BigDecimal.valueOf(323).setScale(2, RoundingMode.HALF_DOWN),
                budget.getBudgetCap());
        assertEquals(true, budget.getActive());
        assertEquals(true, budget.getAutoRevise());
        assertEquals(budget.getDateStarted().plusDays(1).toLocalDate(),
                budget.getDueDate().toLocalDate());
        assertEquals(1L, budget.getOwnerId());
    }

    @Test
    public void testCreate_emptyPayload_shouldThrowException() {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();

        assertThrows(IllegalArgumentException.class, () -> {
            this.budgetService.create(createBudgetDto, 1L);
        });
    }

    @Test
    public void testFindById_validId_shouldReturnBudget() {
        Budget budget = this.budgetService.findById(1001L);

        assertNotNull(budget);
        assertEquals("Food", budget.getBudgetName());
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        assertThrows(ApiException.class, () -> {
            this.budgetService.findById(213L);
        });
    }

    @Test
    public void testIsBudgetValid_validId_shouldReturnTrue() {

        boolean isBudgetValid = this.budgetService.isBudgetValid(1001L);

        assertTrue(isBudgetValid);
    }

    @Test
    public void testIsBudgetValid_invalidId_shouldReturnFalse() {

        boolean isBudgetValid = this.budgetService.isBudgetValid(1006L);

        assertFalse(isBudgetValid);

    }

    @Test
    public void testIsBudgetValid_notActive_shouldReturnFalse() {
        CreateBudgetDto budgetDto = new CreateBudgetDto();
        budgetDto.setBudgetName("Test");
        budgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        budgetDto.setDateStarted(LocalDateTime.now());
        budgetDto.setActive(false);
        budgetDto.setAutoRevise(true);

        this.budgetService.create(budgetDto, 1L);

        assertTrue(this.budgetRepository.findById(1L).isPresent());

        boolean isBudgetValid = this.budgetService.isBudgetValid(1L);

        assertFalse(isBudgetValid);
    }

    @Test
    public void testIsUserPermitted_validOwnerId_shouldReturnTrue() {

        Optional<User> user = this.userRepository.findByUsername("Ignat");

        boolean isUserPermitted = this.budgetService.isUserPermitted(user.get(), 1001L);

        assertTrue(isUserPermitted);
    }

    @Test
    @Transactional
    public void testIsUserPermitted_invalidId_shouldReturnFalse() {

        Optional<User> user = this.userRepository.findByUsername("Coco");

        boolean isUserPermitted = this.budgetService.isUserPermitted(user.get(), 1001L);

        assertFalse(isUserPermitted);

    }

    @Test
    @Transactional
    public void testIsUserPermitted_validParticipantId_shouldReturnTrue() {

        Optional<User> user = this.userRepository.findByUsername("Roza");

        boolean isUserPermitted = this.budgetService.isUserPermitted(user.get(), 1001L);
        assertTrue(isUserPermitted);

    }

    @Test
    @Transactional
    public void testAddParticipant_validPayload_shouldAddParticipant() {

        assertEquals(1, this.budgetService.findById(1001L).getParticipants().size());

        Budget budget = this.budgetService.findById(1001L);

        AssignParticipantDto participantDto = new AssignParticipantDto();
        participantDto.setParticipantId(3L);
        participantDto.setBudgetRef(budget);

        this.budgetService.addParticipant(participantDto);

        assertEquals(2, this.budgetService.findById(1001L).getParticipants().size());
    }

    @Test
    @Transactional
    public void testDeleteParticipant_validPayload_shouldDeleteParticipant() {

        assertEquals(1, this.budgetService.findById(1001L).getParticipants().size());

        Budget budget = this.budgetService.findById(1001L);

        UnassignParticipantDto participantDto = new UnassignParticipantDto();
        participantDto.setParticipantId(2L);
        participantDto.setBudgetRef(budget);

        this.budgetService.deleteParticipant(participantDto);

        assertEquals(0, this.budgetService.findById(1001L).getParticipants().size());
    }

    @Test
    public void testFindIfValid_validId_shouldReturnBudget() {
        Optional<Budget> budget = this.budgetService.findIfValid(1001L);

        assertNotNull(budget);
    }

    @Test
    public void testFindIfValid_invalidId_shouldReturnBudget() {
        Optional<Budget> budget = this.budgetService.findIfValid(10043L);

        assertTrue(budget.isEmpty());
    }

    @Test
    public void testFindByIdAndFetchAll_validId_shouldReturnBudget() {

        Budget budget = this.budgetService.findByIdFetchAll(1001L);

        assertNotNull(budget);
    }

    @Test
    public void testFindByIdAndFetchAll_invalidId_shouldThrowException() {
        assertThrows(ApiException.class, () -> this.budgetService.findByIdFetchAll(1005L));
    }

    @Test
    @Transactional
    public void testSearchBudget_validPayload_shouldReturnBudget() {
        BudgetSearchQuery query = new BudgetSearchQuery();
        SortQuery sortQuery = new SortQuery("id", SortDirection.ASC);
        PageQuery pageQuery = new PageQuery(0, 2);
        User user = this.userService.getById(1L);

        query.setSort(sortQuery);
        query.setPage(pageQuery);

        //Search by name
        query.setBudgetName("Food");

        Page<Budget> budgets = this.budgetService.searchBudget(query, user);

        assertEquals(1, budgets.getTotalElements());
        assertEquals(1001L, budgets.getContent().getFirst().getId());

        query.setBudgetName(null);

        //Search by budgetCap

        BetweenQuery<BigDecimal> bigDecimalBetweenQuery = new BetweenQuery<>(BigDecimal.valueOf(20),
                BigDecimal.valueOf(100));
        query.setBudgetCap(bigDecimalBetweenQuery);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(1, budgets.getTotalElements());
        assertEquals(1001L, budgets.getContent().getFirst().getId());

        query.setBudgetCap(null);

        //Search by balance

        query.setBalance(bigDecimalBetweenQuery);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(2, budgets.getTotalElements());
        assertEquals(1001L, budgets.getContent().getFirst().getId());
        assertEquals(1004L, budgets.getContent().get(1).getId());

        query.setBalance(null);

        //Search by active
        query.setActive(true);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(3, budgets.getTotalElements());
        assertEquals(1001L, budgets.getContent().getFirst().getId());
        assertEquals(1002L, budgets.getContent().get(1).getId());

        // Test page 1
        pageQuery.setPageNumber(1);
        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(1004L, budgets.getContent().getLast().getId());

        //Search active and autoRevise

        pageQuery.setPageNumber(0);
        query.setPage(pageQuery);
        query.setAutoRevise(true);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(2, budgets.getTotalElements());
        assertEquals(1001L, budgets.getContent().getFirst().getId());
        assertEquals(1002L, budgets.getContent().getLast().getId());

        //Search by dateStarted

        BetweenQuery<LocalDateTime> localDateTimeBetweenQuery = new BetweenQuery<>(
                LocalDateTime.of(2019, 1, 1, 0,
                        0, 0), LocalDateTime.now()
        );

        query.setAutoRevise(null);
        query.setActive(null);
        query.setDateStarted(localDateTimeBetweenQuery);
        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(2, budgets.getTotalElements());
        assertEquals(1002L, budgets.getContent().getFirst().getId());
        assertEquals(1004L, budgets.getContent().getLast().getId());

        //Search by dueDate

        query.setDateStarted(null);

        localDateTimeBetweenQuery = new BetweenQuery<>(
                LocalDateTime.of(2020, 1, 1, 0,
                        0, 0), LocalDateTime.now());

        query.setDueDate(localDateTimeBetweenQuery);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(1, budgets.getTotalElements());
        assertEquals(1004L, budgets.getContent().getFirst().getId());

        //Search by dueDate and autoRevise

        query.setAutoRevise(true);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(0, budgets.getTotalElements());

        //Test user 2 and test sort if field not exist

        sortQuery.setField("notExist");

        query.setDueDate(null);
        query.setAutoRevise(null);

        user = this.userService.getById(3L);

        budgets = this.budgetService.searchBudget(query, user);

        assertEquals(1, budgets.getTotalElements());
        assertEquals(1003L, budgets.getContent().getFirst().getId());

    }

    @Test
    @Transactional
    public void testUpdateBudgetAfterRevision_shouldUpdateBudget() {

        Revision revision = this.revisionService.findById(1001L);

        this.budgetService.updateBudgetAfterRevision(revision.getBudgetId(), revision);

        Budget budget = this.budgetService.findById(revision.getBudgetId());

        assertEquals(revision.getRevisionDate(), budget.getDateStarted());
        assertEquals(revision.getBalance(), budget.getBalance());
        assertEquals(budget.getDateStarted().plusDays(1).toLocalDate(),
                budget.getDueDate().toLocalDate());
    }

    @Test
    @Transactional
    public void testGetStatistics_validId_shouldReturnStatistics() {

        BudgetStatistics statistics = this.budgetService.getStatistics(1001L);

        assertNotNull(statistics);
        assertEquals(BigDecimal.valueOf(123.32),
                statistics.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(45.00).setScale(2, RoundingMode.HALF_DOWN),
                statistics.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(101.32),
                statistics.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.ZERO, statistics.getDebtLendedAmount());
        assertEquals(BigDecimal.ZERO, statistics.getDebtReceivedAmount());
    }

    @Test
    @Transactional
    public void testGetStatistics_invalidId_shouldThrowException() {

        assertThrows(ApiException.class, () -> this.budgetService.getStatistics(100231L));

    }

    @Test
    @Transactional
    public void testUpdateBudget_validPayload_shouldUpdateBudget() {
        Budget budget = this.budgetService.findById(1001L);

        assertEquals("Food", budget.getBudgetName());
        assertEquals("FREQ=DAILY;INTERVAL=1", budget.getRecurringRule());
        assertEquals(true, budget.getActive());
        assertEquals(BigDecimal.valueOf(23).setScale(2, RoundingMode.HALF_DOWN),
                budget.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_DOWN),
                budget.getBudgetCap().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(true, budget.getAutoRevise());

        UpdateBudgetDto updateBudgetDto = new UpdateBudgetDto();
        updateBudgetDto.setBudgetName("Pesto");
        updateBudgetDto.setRecurringRule("FREQ=YEARLY;INTERVAL=2");
        updateBudgetDto.setActive(false);
        updateBudgetDto.setBudgetCap(BigDecimal.valueOf(100));
        updateBudgetDto.setAutoRevise(false);

        this.budgetService.update(updateBudgetDto, budget.getId());

        budget = this.budgetService.findById(1001L);

        assertEquals(updateBudgetDto.getBudgetName(), budget.getBudgetName());
        assertEquals(updateBudgetDto.getRecurringRule(), budget.getRecurringRule());
        assertEquals(false, budget.getActive());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_DOWN),
                budget.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(updateBudgetDto.getBudgetCap().setScale(2, RoundingMode.HALF_DOWN),
                budget.getBudgetCap().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(false, budget.getAutoRevise());
    }

    @Test
    @Transactional
    public void testUpdateBudget_invalidId_shouldThrowException() {

        UpdateBudgetDto updateBudgetDto = new UpdateBudgetDto();
        updateBudgetDto.setBudgetName("Pesto");
        updateBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=2");
        updateBudgetDto.setActive(false);
        updateBudgetDto.setBudgetCap(BigDecimal.valueOf(100));
        updateBudgetDto.setBalance(BigDecimal.valueOf(45.22));
        updateBudgetDto.setAutoRevise(false);

        assertThrows(ApiException.class, () -> this.budgetService.update(updateBudgetDto, 10055L));
    }

    @Test
    public void testGetStatisticsFetchAll_validId() {
        BudgetStatistics statistics = this.budgetService.getStatisticsFetchAll(1001L);

        assertNotNull(statistics);
        assertNotNull(statistics.getBudget());
        assertEquals(1001L, statistics.getBudget().getId());
        assertEquals("Food", statistics.getBudget().getBudgetName());

        assertEquals(BigDecimal.valueOf(101.32), statistics.getRealBalance());
    }

    @Test
    public void testGetStatisticsFetchAll_invalidId() {

        AssertUtil.assertApiException(ApiErrorCode.ERR_0004,
                () -> this.budgetService.getStatisticsFetchAll(1004321L)
        );
    }

}

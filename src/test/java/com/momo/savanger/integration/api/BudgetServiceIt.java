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
import com.momo.savanger.api.budget.CreateBudgetDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.api.user.UserRepository;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetServiceIt {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreate_validPayload_shouldCreate() {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();
        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.now());
        createBudgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        createBudgetDto.setBalance(BigDecimal.valueOf(243.4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);

        assertNotNull(this.budgetService.create(createBudgetDto, 1L));

        List<Budget> budgets = this.budgetRepository.findAll();

        assertEquals(2, budgets.size());

        assertThat(List.of("Food", "Test"))
                .hasSameElementsAs(
                        budgets.stream().map(Budget::getBudgetName).toList()
                );
    }

    @Test
    public void testCreate_emptyPayload_shouldThrowException() {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();

        assertThrows(DataIntegrityViolationException.class, () -> {
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

        boolean isBudgetValid = this.budgetService.isBudgetValid(1002L);

        assertFalse(isBudgetValid);

    }

    @Test
    public void testIsBudgetValid_notActive_shouldReturnFalse() {
        CreateBudgetDto budgetDto = new CreateBudgetDto();
        budgetDto.setBudgetName("Test");
        budgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        budgetDto.setDateStarted(LocalDateTime.now());
        budgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        budgetDto.setActive(false);
        budgetDto.setAutoRevise(true);

        this.budgetService.create(budgetDto, 1L);

        boolean isBudgetValid = this.budgetService.isBudgetValid(1L);

        assertFalse(isBudgetValid);
    }

    @Test
    public void testIsUserPermitted_validId_shouldReturnTrue() {

        User user = this.userRepository.findByUsername("Ignat");

        boolean isBudgetValid = this.budgetService.isUserPermitted(user, 1001L);

        assertTrue(isBudgetValid);
    }

    @Test
    public void testIsUserPermitted_invalidId_shouldReturnFalse() {

        User user = this.userRepository.findByUsername("Roza");

        boolean isBudgetValid = this.budgetService.isUserPermitted(user, 1001L);

        assertFalse(isBudgetValid);

    }

}

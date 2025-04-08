package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.CreateBudgetDto;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:/sql/budget-it-data.sql")
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetServiceIt {

    @Autowired
    private BudgetService budgetService;

    @Test
    public void testSaveBudget_validPayload_shouldSaveBudget() {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();
        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.now());
        createBudgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        createBudgetDto.setBalance(BigDecimal.valueOf(243.4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);

        assertNotNull(this.budgetService.saveBudget(createBudgetDto, 1L));

        List<Budget> budgets = this.budgetService.findAllBudgets();

        assertEquals(2, budgets.size());

        assertArrayEquals(List.of("Food", "Test").toArray(),
                budgets.stream().map(budget -> budget.getBudgetName()).toArray());
    }

    @Test
    public void testSaveBudget_emptyPayload_shouldReturnNull() {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();

        assertNull(this.budgetService.saveBudget(createBudgetDto, 1L));
    }

    @Test
    public void testFindBudgetById_validId_shouldReturnBudget() {
        Budget budget = this.budgetService.findBudgetById(1L);

        assertNotNull(budget);
        assertEquals("Food", budget.getBudgetName());
    }

    @Test
    public void testFindAllBudgets_shouldReturnAllBudgets() {
        List<Budget> budgets = this.budgetService.findAllBudgets();

        assertEquals(1, budgets.size());
    }

}

package com.momo.savanger.integration.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.revision.CreateRevisionDto;
import com.momo.savanger.api.revision.Revision;
import com.momo.savanger.api.revision.RevisionRepository;
import com.momo.savanger.api.revision.RevisionService;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
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
@Sql("classpath:/sql/revision-it-data.sql")
@Sql(value = "classpath:/sql/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class RevisionServiceIt {

    @Autowired
    private RevisionService revisionService;

    @Autowired
    private RevisionRepository revisionRepository;

    @Autowired
    private BudgetService budgetService;

    @Test
    public void testCreate_lessDtoBalance_shouldCreateRevision() {
        CreateRevisionDto dto = new CreateRevisionDto();
        dto.setBalance(BigDecimal.valueOf(23.32));
        dto.setBudgetId(1001L);

        List<Revision> revisions = this.revisionRepository.findAll();

        assertEquals(1, revisions.size());

        Revision revision = this.revisionService.create(dto);

        revisions = this.revisionRepository.findAll();

        assertEquals(2, revisions.size());

        Budget budget = this.budgetService.findById(revision.getBudgetId());

        assertEquals(BigDecimal.valueOf(23.32),
                revision.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(1001L, revision.getBudgetId());
        assertEquals(budget.getBudgetCap(), revision.getBudgetCap());
        assertEquals(budget.getDateStarted().truncatedTo(ChronoUnit.SECONDS), revision.getRevisionDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(false, revision.getAutoRevise());
        assertEquals(BigDecimal.valueOf(123.32),
                revision.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(45.00).setScale(2, RoundingMode.HALF_DOWN),
                revision.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(-78.00).setScale(2, RoundingMode.HALF_DOWN),
                revision.getCompensationAmount().setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testCreate_biggerDtoBalance_shouldCreateRevision() {
        CreateRevisionDto dto = new CreateRevisionDto();
        dto.setBalance(BigDecimal.valueOf(239.32));
        dto.setBudgetId(1001L);

        List<Revision> revisions = this.revisionRepository.findAll();

        assertEquals(1, revisions.size());

        Revision revision = this.revisionService.create(dto);

        revisions = this.revisionRepository.findAll();

        assertEquals(2, revisions.size());

        Budget budget = this.budgetService.findById(revision.getBudgetId());

        assertEquals(BigDecimal.valueOf(239.32),
                revision.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(1001L, revision.getBudgetId());
        assertEquals(budget.getBudgetCap(), revision.getBudgetCap());
        assertEquals(budget.getDateStarted().truncatedTo(ChronoUnit.SECONDS), revision.getRevisionDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(false, revision.getAutoRevise());
        assertEquals(BigDecimal.valueOf(123.32),
                revision.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(45.00).setScale(2, RoundingMode.HALF_DOWN),
                revision.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(138.00).setScale(2, RoundingMode.HALF_DOWN),
                revision.getCompensationAmount().setScale(2, RoundingMode.HALF_DOWN));
    }

    @Test
    public void testCreate_withoutDtoBalance_shouldCreateRevision() {
        CreateRevisionDto dto = new CreateRevisionDto();
        dto.setBudgetId(1001L);

        List<Revision> revisions = this.revisionRepository.findAll();

        assertEquals(1, revisions.size());

       Revision revision =  this.revisionService.create(dto);

        revisions = this.revisionRepository.findAll();

        assertEquals(2, revisions.size());

        Budget budget = this.budgetService.findById(revision.getBudgetId());

        assertEquals(BigDecimal.valueOf(101.32),
                revision.getBalance().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(1001L, revision.getBudgetId());
        assertEquals(budget.getBudgetCap(), revision.getBudgetCap());
        assertEquals(budget.getDateStarted().truncatedTo(ChronoUnit.SECONDS), revision.getRevisionDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(false, revision.getAutoRevise());
        assertEquals(BigDecimal.valueOf(123.32),
                revision.getEarningsAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertEquals(BigDecimal.valueOf(45.00).setScale(2, RoundingMode.HALF_DOWN),
                revision.getExpensesAmount().setScale(2, RoundingMode.HALF_DOWN));
        assertNull(revision.getCompensationAmount());
    }

    @Test
    public void testCreate_invalidBudgetId_shouldThrowException() {
        CreateRevisionDto dto = new CreateRevisionDto();
        dto.setBudgetId(23232L);
        dto.setBalance(BigDecimal.valueOf(434.00));

        assertThrows(
                ApiException.class, () -> this.revisionService.create(dto)
        );
    }

    @Test
    public void testCreate_emptyPayload_shouldThrowException() {
        CreateRevisionDto dto = new CreateRevisionDto();

        assertThrows(
                InvalidDataAccessApiUsageException.class, () -> this.revisionService.create(dto)
        );
    }

    @Test
    public void testFindById_validId_shouldReturnRevision() {
        Revision revision = this.revisionService.findById(1001L);

        assertEquals(1001L, revision.getId());
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        assertThrows(ApiException.class, () -> this.revisionService.findById(1L));
    }

}

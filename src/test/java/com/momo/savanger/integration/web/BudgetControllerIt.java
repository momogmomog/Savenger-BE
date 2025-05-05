package com.momo.savanger.integration.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.budget.Budget;
import com.momo.savanger.api.budget.BudgetRepository;
import com.momo.savanger.api.budget.BudgetService;
import com.momo.savanger.api.budget.dto.CreateBudgetDto;
import com.momo.savanger.api.user.User;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/budgets_participants-it-data.sql")
@Sql(value = "classpath:/sql/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class BudgetControllerIt extends BaseControllerIt {

    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private BudgetService budgetService;

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_validPayload_shouldSaveBudget() throws Exception {

        CreateBudgetDto createBudgetDto = new CreateBudgetDto();
        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.now());
        createBudgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        createBudgetDto.setBalance(BigDecimal.valueOf(243.4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);

        super.postOK(Endpoints.BUDGETS, createBudgetDto);

        List<Budget> budgets = this.budgetRepository.findAll();

        assertThat(List.of("Food", "Test"))
                .hasSameElementsAs(
                        budgets.stream().map(Budget::getBudgetName).toList()
                );
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_InvalidPayload() throws Exception {
        CreateBudgetDto createBudgetDto = new CreateBudgetDto();

        super.post(
                Endpoints.BUDGETS,
                createBudgetDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(8)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetName\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"dateStarted\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"dueDate\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"active\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"autoRevise\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"RRule\")]").exists()
        );

        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREuQ=DAILY;INTERVAL=1");
        createBudgetDto.setDateStarted(LocalDateTime.now());
        createBudgetDto.setDueDate(LocalDateTime.now().plusMonths(5));
        createBudgetDto.setBalance(BigDecimal.valueOf(-4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(-323));
        createBudgetDto.setActive(true);
        createBudgetDto.setAutoRevise(true);

        super.post(
                Endpoints.BUDGETS,
                createBudgetDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"balance\" && @.constraintName == \"MinValueZero\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetCap\" && @.constraintName == \"MinValueZero\")]").exists(),

                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"RRule\")]").exists()
        );

        createBudgetDto.setRecurringRule("");
        createBudgetDto.setBudgetName("");
        createBudgetDto.setBalance(BigDecimal.valueOf(4));
        createBudgetDto.setBudgetCap(BigDecimal.valueOf(323));

        super.post(
                Endpoints.BUDGETS,
                createBudgetDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"RRule\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"recurringRule\" && @.constraintName == \"Length\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetName\" && @.constraintName == \"LengthName\")]").exists()
        );

        createBudgetDto.setBudgetName("Test");
        createBudgetDto.setRecurringRule("FREQ=DAILY;INTERVAL=1");

        final var payloadMap = new HashMap<String, String>();
        payloadMap.put("dateStarted", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        super.post(
                Endpoints.BUDGETS,
                payloadMap,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.[?(@.field == \"dateStarted\" && @.constraintName == \"NotNull\")]").doesNotExist()
        );

        payloadMap.put("dateStarted", "el data");

        super.post(
                Endpoints.BUDGETS,
                payloadMap,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.[?(@.field == \"dateStarted\" && @.constraintName == \"NotNull\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testAddParticipant_validPayload_shouldAddParticipantToBudget() throws Exception {

        assertEquals(1, this.budgetService.findIfValid(1001L).get().getParticipants().size());

        Map<String, Long> data = new HashMap<>();
        data.put("participantId", 3L);
        data.put("budgetId", 1001L);

        super.postOK("/budgets/1001/participants", data);

        assertEquals(2, this.budgetService.findIfValid(1001L).get().getParticipants().size());
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testAddParticipant_invalidBudgetOwner_shouldThrowException() throws Exception {

        Map<String, Long> data = new HashMap<>();
        data.put("participantId", 3L);
        data.put("budgetId", 1001L);

        super.post("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetRef\" && @.constraintName == \"IsBudgetOwner\")]").exists());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testAddParticipant_InvalidPayload_shouldThrowException() throws Exception {

        Map<String, Long> data = new HashMap<>();

        data.put("participantId", 3L);
        data.put("budgetId", 100L);

        super.post("/budgets/100/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"Missing or invalid budget\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetRef\" && @.constraintName == \"NotNull\")]").exists()
        );

        data.put("budgetId", 1001L);
        data.put("participantId", 4L);

        super.post("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"participantId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"User does not exist.\")]").exists()
        );

        data.put("participantId", 1L);

        super.post("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"participantId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"Owner cannot be edit.\")]").exists()
        );

        data.put("participantId", 2L);

        super.post("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"participantId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"This user is already a participant.\")]").exists()
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testDeleteParticipant_validPayload_shouldDeleteParticipantSuccessfully() throws Exception {

        assertEquals(1, this.budgetService.findIfValid(1001L).get().getParticipants().size());

        Map<String, Long> data = new HashMap<>();
        data.put("participantId", 2L);
        data.put("budgetId", 1001L);

        super.deleteOK("/budgets/1001/participants", data);

        assertEquals(0, this.budgetService.findIfValid(1001L).get().getParticipants().size());
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testDeleteParticipant_invalidBudgetOwner_shouldThrowException() throws Exception {

        Map<String, Long> data = new HashMap<>();
        data.put("participantId", 2L);
        data.put("budgetId", 1001L);

        super.delete("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetRef\" && @.constraintName == \"IsBudgetOwner\")]").exists());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testDeleteParticipant_InvalidPayload_shouldThrowException() throws Exception {

        Map<String, Long> data = new HashMap<>();

        data.put("participantId", 3L);
        data.put("budgetId", 100L);

        super.delete("/budgets/100/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"Missing or invalid budget\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetRef\" && @.constraintName == \"NotNull\")]").exists()
        );

        data.put("budgetId", 1001L);
        data.put("participantId", 4L);

        super.delete("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"participantId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"User does not exist.\")]").exists()
        );

        data.put("participantId", 1L);

        super.delete("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"participantId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"Owner cannot be edit.\")]").exists()
        );

        data.put("participantId", 3L);

        super.delete("/budgets/1001/participants",
                data,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"participantId\" && @.constraintName == \"AssignParticipantValidation\" && @.message == \"Participant does not exist.\")]").exists()
        );

    }

}

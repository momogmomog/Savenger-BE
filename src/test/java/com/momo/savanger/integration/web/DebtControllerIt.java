package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.debt.CreateDebtDto;
import com.momo.savanger.api.debt.DebtRepository;
import com.momo.savanger.api.debt.PayDebtDto;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/debt/budget-it-data.sql")
@Sql("classpath:/sql/debt/tag-it-data.sql")
@Sql("classpath:/sql/debt/category-it-data.sql")
@Sql("classpath:/sql/debt/transaction-it-data.sql")
@Sql("classpath:/sql/debt/transactions_tags-it-data.sql")
@Sql("classpath:/sql/debt/revision-it-data.sql")
@Sql("classpath:/sql/debt/debt-it-data.sql")
@Sql(value = "classpath:/sql/debt/del-revision-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-transactions_tags-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-transaction-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-debt-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-category-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/debt/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class DebtControllerIt extends BaseControllerIt {

    @Autowired
    private DebtRepository debtRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreateDebt() throws Exception {

        assertEquals(1, this.debtRepository.findAll().size());

        CreateDebtDto dto = new CreateDebtDto();
        dto.setDebtAmount(BigDecimal.valueOf(233.00));
        dto.setReceiverBudgetId(1002L);
        dto.setLenderBudgetId(1001L);

        super.postOK(Endpoints.DEBTS, dto);

        assertEquals(2, this.debtRepository.findAll().size());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload() throws Exception {
        CreateDebtDto dto = new CreateDebtDto();

        //Test empty payload
        super.post(Endpoints.DEBTS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"lenderBudgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"receiverBudgetId\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"debtAmount\" && @.constraintName == \"NotNull\")]").exists()
        );

        // Test equals budgetId

        dto.setLenderBudgetId(1001L);
        dto.setReceiverBudgetId(1001L);

        super.post(Endpoints.DEBTS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"lenderBudgetId\" && @.constraintName == \"ValidDebtDto\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"debtAmount\" && @.constraintName == \"NotNull\")]").exists()
        );

        //Test negative amount
        dto.setReceiverBudgetId(1002L);
        dto.setDebtAmount(BigDecimal.valueOf(-12));

        super.post(Endpoints.DEBTS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"debtAmount\" && @.constraintName == \"MinValueZero\")]").exists()
        );


    }


    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_notOwner() throws Exception {
        CreateDebtDto dto = new CreateDebtDto();

        dto.setDebtAmount(BigDecimal.TEN);
        dto.setLenderBudgetId(1002L);
        dto.setReceiverBudgetId(1001L);

        //Test empty payload
        super.post(Endpoints.DEBTS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"lenderBudgetId\" && @.constraintName == \"CanAccessBudget\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"receiverBudgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testPay_invalidPayload() throws Exception {
        PayDebtDto dto = new PayDebtDto();

        super.post("/debts/101/pay",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"NotNull\")]").exists()
        );

        dto.setAmount(BigDecimal.valueOf(-23));

        super.post("/debts/101/pay",
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"amount\" && @.constraintName == \"MinValueZero\")]").exists()
        );
    }
}

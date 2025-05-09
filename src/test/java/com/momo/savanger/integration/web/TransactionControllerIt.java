package com.momo.savanger.integration.web;


import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.transaction.CreateTransactionDto;
import com.momo.savanger.api.transaction.TransactionRepository;
import com.momo.savanger.api.transaction.TransactionType;
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

        assertEquals(1, this.transactionRepository.findAll().size());

        CreateTransactionDto dto = new CreateTransactionDto();
        dto.setType(TransactionType.EXPENSE);
        dto.setAmount(BigDecimal.valueOf(43.33));
        dto.setBudgetId(1001L);
        dto.setCategoryId(1001L);

        List<Long> ids = new ArrayList<>();
        ids.add(1001L);
        ids.add(1002L);

        dto.setTagIds(ids);
        super.postOK(Endpoints.TRANSACTIONS, dto);

        assertEquals(2, this.transactionRepository.findAll().size());

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
        dto.setBudgetId(1002L);

        super.post(Endpoints.TRANSACTIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanEditBudget\")]").exists(),
                jsonPath("fieldErrors.[?(@.field == \"categoryId\" && @.constraintName == \"ValidTransactionDto\")]").exists()
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
}

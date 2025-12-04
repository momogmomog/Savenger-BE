package com.momo.savanger.integration.web;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.revision.CreateRevisionDto;
import com.momo.savanger.api.revision.RevisionRepository;
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
public class RevisionControllerIt extends BaseControllerIt {

    @Autowired
    private RevisionRepository revisionRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayload_shouldCreateRevision() throws Exception {
        assertEquals(1, revisionRepository.findAll().size());

        CreateRevisionDto dto = new CreateRevisionDto();

        dto.setBalance(BigDecimal.valueOf(54.33));
        dto.setBudgetId(1001L);
        dto.setComment("test");

        super.postOK(Endpoints.REVISIONS, dto);

        assertEquals(2, revisionRepository.findAll().size());
    }

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_invalidPayload() throws Exception {
        CreateRevisionDto dto = new CreateRevisionDto();

        String text =
                "Text messaging, or texting, is the act of composing and sending electronic messages, typically consisting of alphabetic and numeric characters, between two or more users of mobile phones, tablet computers, smartwatches, desktops/laptops, or another type of compatible computer. Text messages may be sent over a cellular network or may also be sent via satellite or Internet connection.\n"
                        + "\n"
                        + "The term originally referred to messages sent using the Short Message Service (SMS) on mobile devices. It has grown beyond alphanumeric text to include multimedia messages using the Multimedia Messaging Service (MMS) and Rich Communication Services (RCS), which can contain digital images, videos, and sound content, as well as ideograms known as emoji (happy faces, sad faces, and other icons), and on various instant messaging apps. Text messaging has been an extremely popular medium of communication since the turn of the century and has also influenced changes in society.";

        super.post(Endpoints.REVISIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath(
                        "fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

        dto.setBudgetId(1001L);
        dto.setBalance(BigDecimal.valueOf(-32.33));

        super.post(Endpoints.REVISIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"balance\" && @.constraintName == \"MinValueZero\")]").exists()
        );

        dto.setBalance(BigDecimal.valueOf(3.21));
        dto.setComment(text);

        super.post(Endpoints.REVISIONS,
                dto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(1)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"comment\" && @.constraintName == \"Length\")]").exists()
        );
    }
}

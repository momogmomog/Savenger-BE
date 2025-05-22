package com.momo.savanger.integration.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.momo.savanger.api.tag.CreateTagDto;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagRepository;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import java.util.List;
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
@Sql("classpath:/sql/tag-it-data.sql")
@Sql("classpath:/sql/budgets_participants-it-data.sql")
@Sql(value = "classpath:/sql/del-budgets_participants-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class TagControllerIt extends BaseControllerIt {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @WithLocalMockedUser(username = Constants.FIRST_USER_USERNAME)
    public void testCreate_validPayloadWithLoggedOwner_shouldSaveTag() throws Exception {

        CreateTagDto tagDto = new CreateTagDto();
        tagDto.setTagName("Test");
        tagDto.setBudgetCap(BigDecimal.valueOf(323));
        tagDto.setBudgetId(1001L);

        super.postOK(Endpoints.TAGS, tagDto);

        List<Tag> tags = this.tagRepository.findAll();

        assertThat(List.of("DM", "Test", "Tok"))
                .hasSameElementsAs(
                        tags.stream().map(Tag::getTagName).toList()
                );
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_validPayloadWithLoggedParticipant_shouldSaveTag() throws Exception {

        CreateTagDto tagDto = new CreateTagDto();
        tagDto.setTagName("Test");
        tagDto.setBudgetCap(BigDecimal.valueOf(323));
        tagDto.setBudgetId(1001L);

        super.postOK(Endpoints.TAGS, tagDto);

        List<Tag> tags = this.tagRepository.findAll();

        assertThat(List.of("DM", "Test", "Tok"))
                .hasSameElementsAs(
                        tags.stream().map(Tag::getTagName).toList()
                );
    }

    @Test
    @WithLocalMockedUser(username = Constants.SECOND_USER_USERNAME)
    public void testCreate_EmptyPayload() throws Exception {
        CreateTagDto tagDto = new CreateTagDto();

        super.post(
                Endpoints.TAGS,
                tagDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"tagName\" && @.constraintName == \"NotNull\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"tagName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"NotNull\")]").exists()
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_InvalidPayload() throws Exception {
        CreateTagDto tagDto = new CreateTagDto();
        tagDto.setTagName("");
        tagDto.setBudgetId(1001L);
        tagDto.setBudgetCap(BigDecimal.valueOf(-43));

        super.post(
                Endpoints.TAGS,
                tagDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(3)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetCap\" && @.constraintName == \"MinValueZero\")]").exists(),

                jsonPath(
                        "fieldErrors.[?(@.field == \"tagName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

    }

    @Test
    @WithLocalMockedUser(username = Constants.THIRD_USER_USERNAME)
    public void testCreate_zeroBudgetCap() throws Exception {
        CreateTagDto tagDto = new CreateTagDto();
        tagDto.setTagName("");
        tagDto.setBudgetId(1001L);
        tagDto.setBudgetCap(BigDecimal.valueOf(0));

        super.post(
                Endpoints.TAGS,
                tagDto,
                HttpStatus.BAD_REQUEST,
                jsonPath("fieldErrors.length()", is(2)),
                jsonPath(
                        "fieldErrors.[?(@.field == \"tagName\" && @.constraintName == \"LengthName\")]").exists(),
                jsonPath(
                        "fieldErrors.[?(@.field == \"budgetId\" && @.constraintName == \"CanAccessBudget\")]").exists()
        );

    }
}

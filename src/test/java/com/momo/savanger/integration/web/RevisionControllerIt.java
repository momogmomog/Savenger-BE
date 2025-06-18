package com.momo.savanger.integration.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.momo.savanger.api.revision.CreateRevisionDto;
import com.momo.savanger.api.revision.RevisionRepository;
import com.momo.savanger.constants.Endpoints;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

}

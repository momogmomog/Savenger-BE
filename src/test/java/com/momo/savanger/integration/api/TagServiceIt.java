package com.momo.savanger.integration.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.tag.CreateTagDto;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagRepository;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.error.ApiException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("classpath:/sql/user-it-data.sql")
@Sql("classpath:/sql/budget-it-data.sql")
@Sql("classpath:/sql/tag-it-data.sql")
@Sql(value = "classpath:/sql/del-tag-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-budget-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@Sql(value = "classpath:/sql/del-user-it-data.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class TagServiceIt {

    @Autowired
    private TagService tagService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void testCreate_validPayload_shouldSaveTag() {
        CreateTagDto tagDto = new CreateTagDto();
        tagDto.setTagName("Voda");
        tagDto.setBudgetId(1001L);

        this.tagService.create(tagDto);

        List<Tag> tags = this.tagRepository.findAll();

        assertThat(List.of("DM", "Tok", "Voda"))
                .hasSameElementsAs(
                        tags.stream().map(Tag::getTagName).toList()
                );
    }

    @Test
    public void testCreate_emptyPayload_shouldThrowException() {
        CreateTagDto tagDto = new CreateTagDto();

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.tagService.create(tagDto);
        });
    }

    @Test
    public void testFindById_validId_shouldReturnBudget() {
        Tag tag = this.tagService.findById(1001L);

        assertNotNull(tag);
        assertEquals("DM", tag.getTagName());
    }

    @Test
    public void testFindById_invalidId_shouldThrowException() {
        assertThrows(ApiException.class, () -> {
            this.tagService.findById(544L);
        });
    }
}

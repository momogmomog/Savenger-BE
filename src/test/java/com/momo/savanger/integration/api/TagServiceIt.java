package com.momo.savanger.integration.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.momo.savanger.api.tag.CreateTagDto;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagQuery;
import com.momo.savanger.api.tag.TagRepository;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.util.BetweenQuery;
import com.momo.savanger.api.util.PageQuery;
import com.momo.savanger.api.util.SortDirection;
import com.momo.savanger.api.util.SortQuery;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
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

        assertThat(List.of("DM", "Tok", "Kotki", "Voda"))
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

    @Test
    public void testSearchTags_valid_shouldReturnTags() {
        CreateTagDto dto = new CreateTagDto();
        dto.setTagName("Smetki");
        dto.setBudgetId(1001L);
        dto.setBudgetCap(BigDecimal.valueOf(100));

        Tag tag = this.tagService.create(dto);

        TagQuery query = new TagQuery();

        PageQuery pageQuery = new PageQuery(0, 3);

        SortQuery sortQuery = new SortQuery("ds", SortDirection.ASC);

        // Search by budgetId
        query.setSort(sortQuery);
        query.setPage(pageQuery);
        query.setBudgetId(1001L);

        Page<Tag> tags = this.tagService.searchTags(query);

        assertEquals(3, tags.getTotalElements());

        assertEquals(tag.getId(), tags.getContent().getFirst().getId());
        assertEquals(1003L, tags.getContent().getLast().getId());

        //Test by name

        query.setBudgetId(1001L);
        query.setTagName("Smetki");

        tags = this.tagService.searchTags(query);

        assertEquals(1, tags.getTotalElements());

        assertEquals("Smetki", tags.getContent().getFirst().getTagName());

        // Test by budget cap

        query.setTagName(null);

        BetweenQuery<BigDecimal> budgetCap = new BetweenQuery<>(BigDecimal.valueOf(0),
                BigDecimal.valueOf(500));

        query.setBudgetCap(budgetCap);
        tags = this.tagService.searchTags(query);

        assertEquals(3, tags.getTotalElements());

        assertEquals("Smetki", tags.getContent().getFirst().getTagName());
        assertEquals("Kotki", tags.getContent().getLast().getTagName());

        //Test budget cap 2
        budgetCap = new BetweenQuery<>(BigDecimal.valueOf(20), BigDecimal.valueOf(299));

        query.setBudgetCap(budgetCap);

        tags = this.tagService.searchTags(query);

        assertEquals(1, tags.getTotalElements());
        assertEquals("Smetki", tags.getContent().getFirst().getTagName());
    }

    @Test
    public void testFindByBudgetIdAndIdContaining_valid() {

        List<Long> tagIds = new ArrayList<>();
        tagIds.add(1001L);

        List<Tag> foundTagIds = this.tagService.findByBudgetAndIdContaining(tagIds, 1001L);

        assertEquals(1, foundTagIds.size());
        assertEquals(1001L, foundTagIds.getFirst().getId());
        assertEquals("DM", foundTagIds.getFirst().getTagName());

        tagIds.add(1003L);

        foundTagIds = this.tagService.findByBudgetAndIdContaining(tagIds, 1001L);
        assertEquals(2, foundTagIds.size());
        assertEquals(1001L, foundTagIds.getFirst().getId());
        assertEquals("DM", foundTagIds.getFirst().getTagName());
        assertEquals(1003L, foundTagIds.getLast().getId());
        assertEquals("Kotki", foundTagIds.getLast().getTagName());
    }

    @Test
    public void testFindByBudgetIdAndIdContaining_invalid() {

        //With empty tagIds
        List<Long> tagIds = new ArrayList<>();
        List<Tag> foundTagIds = this.tagService.findByBudgetAndIdContaining(tagIds, 1001L);

        assertEquals(0, foundTagIds.size());

        //with invalid budget id

        tagIds.add(1001L);
        foundTagIds = this.tagService.findByBudgetAndIdContaining(tagIds, 100433L);

        assertEquals(0, foundTagIds.size());


    }
}

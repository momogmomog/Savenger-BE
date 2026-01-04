package com.momo.savanger.api.tag;

import java.util.List;
import org.springframework.data.domain.Page;

public interface TagService {

    Tag findById(Long id);

    Tag create(CreateTagDto createTagDto);

    List<Tag> findByBudgetAndIdContaining(List<Long> tagsId, Long budgetId);

    Page<Tag> searchTags(TagQuery query);
}

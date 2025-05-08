package com.momo.savanger.api.tag;

import java.util.List;

public interface TagService {

    Tag findById(Long id);

    Tag create(CreateTagDto createTagDto);

    List<Tag> findByBudgetAndIdContaining(List<Long> tagsId, Long budgetId);

}

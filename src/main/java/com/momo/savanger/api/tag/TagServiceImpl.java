package com.momo.savanger.api.tag;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Tag findById(Long id) {
        return this.tagRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0006));
    }

    @Override
    public Tag create(CreateTagDto createTagDto) {
        final Tag tag = this.tagMapper.toTag(createTagDto);

        if (tag.getBudgetCap() == null) {
            tag.setBudgetCap(BigDecimal.ZERO);
        }

        this.tagRepository.saveAndFlush(tag);

        return this.findById(tag.getId());
    }

    @Override
    public List<Tag> findByBudgetAndIdContaining(List<Long> tagIds, Long budgetId) {
        final Specification<Tag> specification = TagSpecification.idIn(tagIds)
                .and(TagSpecification.budgetIdEquals(budgetId));

        return this.tagRepository.findAll(specification, null);
    }
}

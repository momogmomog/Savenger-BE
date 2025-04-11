package com.momo.savanger.api.tag;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Tag findById(Long id) {
        return this.tagRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0004));
    }

    @Override
    public Tag create(CreateTagDto createTagDto) {
        Tag tag = this.tagMapper.toTag(createTagDto);

        if (tag.getBudgetCap() == null) {
            tag.setBudgetCap(BigDecimal.ZERO);
        }

        this.tagRepository.saveAndFlush(tag);

        return this.findById(tag.getId());
    }
}

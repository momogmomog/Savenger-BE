package com.momo.savanger.web;

import com.momo.savanger.api.tag.CreateTagDto;
import com.momo.savanger.api.tag.TagDto;
import com.momo.savanger.api.tag.TagMapper;
import com.momo.savanger.api.tag.TagQuery;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class TagController {

    private final TagService tagService;

    private final TagMapper tagMapper;

    @PostMapping(Endpoints.TAGS)
    public TagDto create(@Valid @RequestBody CreateTagDto createTagDto) {

        return this.tagMapper.toTagDto(this.tagService.create(createTagDto));
    }

    @PostMapping(Endpoints.TAGS_SEARCH)
    public PagedModel<TagDto> searchTags(@Valid @RequestBody TagQuery query) {
        return new PagedModel<>(this.tagService.searchTags(query).map(this.tagMapper::toTagDto));
    }

}

package com.momo.savanger.api.tag;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    Tag toTag(CreateTagDto tagDto);

    TagDto toTagDto(Tag tag);
}

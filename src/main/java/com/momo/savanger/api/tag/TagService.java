package com.momo.savanger.api.tag;

public interface TagService {

    Tag findById(Long id);

    Tag create(CreateTagDto createTagDto);

}

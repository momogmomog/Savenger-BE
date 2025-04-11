package com.momo.savanger.api.category;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CreateCategoryDto categoryCreateDto);

    CategoryDto toCategoryDto(Category category);

}

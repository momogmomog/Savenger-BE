package com.momo.savanger.api.category;

public interface CategoryService {

    Category findById(Long id);

    Category create(CreateCategoryDto categoryDto);

}

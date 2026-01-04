package com.momo.savanger.api.category;

import org.springframework.data.domain.Page;

public interface CategoryService {

    Category findById(Long id);

    Category create(CreateCategoryDto categoryDto);

    boolean isCategoryValid(Long categoryId, Long budgetId);

    Page<Category> searchCategories(CategoryQuery query);
}

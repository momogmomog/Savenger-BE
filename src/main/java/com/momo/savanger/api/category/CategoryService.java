package com.momo.savanger.api.category;

import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {

    Category findById(Long id);

    Category create(CreateCategoryDto categoryDto);

    boolean isCategoryValid(Long categoryId, Long budgetId);

    Page<Category> searchCategories(CategoryQuery query);

    List<Category> findAll(Long budgetId, List<Long> categoryIds);
}

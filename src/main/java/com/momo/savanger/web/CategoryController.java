package com.momo.savanger.web;


import com.momo.savanger.api.category.CategoryDto;
import com.momo.savanger.api.category.CategoryMapper;
import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.category.CreateCategoryDto;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class CategoryController {

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    @PostMapping(Endpoints.CATEGORIES)
    public CategoryDto create(@Valid @RequestBody CreateCategoryDto categoryDto) {

        return this.categoryMapper.toCategoryDto(this.categoryService.create(categoryDto));
    }
}

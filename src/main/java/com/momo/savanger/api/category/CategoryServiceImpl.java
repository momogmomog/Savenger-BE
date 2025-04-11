package com.momo.savanger.api.category;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper mapper;

    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id).orElse(null);
    }

    @Override
    public Category create(CreateCategoryDto categoryDto) {

        final Category category = this.mapper.toCategory(categoryDto);

        if (category.getBudgetCap() == null) {
            category.setBudgetCap(BigDecimal.ZERO);
        }

        this.categoryRepository.saveAndFlush(category);

        return this.findById(category.getId());
    }
}

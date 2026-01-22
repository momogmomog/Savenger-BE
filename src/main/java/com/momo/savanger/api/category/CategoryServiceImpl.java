package com.momo.savanger.api.category;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final CategoryMapper mapper;

    @Override
    public Category findById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.with(ApiErrorCode.ERR_0005));
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

    @Override
    public boolean isCategoryValid(Long categoryId, Long budgetId) {
        final Specification<Category> specification = CategorySpecification.idEquals(categoryId)
                .and(CategorySpecification.budgetIdEquals(budgetId));

        return this.categoryRepository.exists(specification);
    }

    @Override
    public Page<Category> searchCategories(CategoryQuery query) {
        final Specification<Category> specification = CategorySpecification
                .budgetIdEquals(query.getBudgetId())
                .and(CategorySpecification.nameContains(query.getCategoryName()))
                .and(CategorySpecification.capBetween(query.getBudgetCap()))
                .and(CategorySpecification.sort(query.getSort()));

        return this.categoryRepository.findAll(specification, query.getPage(), null);
    }

    @Override
    public List<Category> findAll(Long budgetId, List<Long> categoryIds) {
        final Specification<Category> specification = CategorySpecification
                .budgetIdEquals(budgetId)
                .and(CategorySpecification.idIn(categoryIds));

        return this.categoryRepository.findAll(specification, null);
    }
}

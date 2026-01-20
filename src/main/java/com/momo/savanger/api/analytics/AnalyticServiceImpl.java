package com.momo.savanger.api.analytics;

import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricDto;
import com.momo.savanger.api.category.Category;
import com.momo.savanger.api.category.CategoryMapper;
import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagMapper;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticsService {

    private final TransactionService transactionService;

    private final CategoryService categoryService;

    private final CategoryMapper categoryMapper;

    private final TagService tagService;

    private final TagMapper tagMapper;

    @Override
    public List<CategoryAnalytic> fetchCategoryAnalytics(TransactionSearchQuery query) {
        final List<Long> categoryIds;
        if (!CollectionUtils.isEmpty(query.getCategoryIds())) {
            log.info("Using provided category IDs");
            categoryIds = query.getCategoryIds();
        } else {
            log.info("No category IDs provided, fetching based on transaction query.");
            categoryIds = this.transactionService.extractCategoryIds(query);
        }

        if (categoryIds.isEmpty()) {
            return List.of();
        }

        final List<Category> categories = this.categoryService.findAll(query.getBudgetId(),
                categoryIds);
        if (categories.isEmpty()) {
            log.info("No categories found, aborting!");
            return List.of();
        }

        final List<CategoryAnalytic> result = new ArrayList<>();

        for (Category category : categories) {
            query.setCategoryIds(List.of(category.getId()));
            query.setType(TransactionType.EXPENSE);
            final BigDecimal expenses = this.transactionService.sum(query);

            query.setType(TransactionType.INCOME);
            final BigDecimal incomes = this.transactionService.sum(query);

            result.add(new CategoryAnalytic(
                    this.categoryMapper.toCategoryDto(category),
                    incomes,
                    expenses,
                    0L // TODO: think of a way to add this value without making a third query
            ));
        }

        return result;
    }

    @Override
    public List<TagAnalytic> fetchTagAnalytics(TransactionSearchQuery query) {
        final List<Long> tagIds;
        if (!CollectionUtils.isEmpty(query.getTagIds())) {
            log.info("Using provided tag IDs");
            tagIds = query.getTagIds();
        } else {
            log.info("No tag IDs provided, fetching based on transaction query.");
            tagIds = this.transactionService.extractTagIds(query);
        }

        if (tagIds.isEmpty()) {
            return List.of();
        }

        final List<Tag> tags = this.tagService.findByBudgetAndIdContaining(tagIds,
                query.getBudgetId());
        if (tags.isEmpty()) {
            log.info("No tags found, aborting!");
            return List.of();
        }

        final List<TagAnalytic> result = new ArrayList<>();
        for (Tag tag : tags) {
            query.setTagIds(List.of(tag.getId()));

            query.setType(TransactionType.EXPENSE);
            final BigDecimal expenses = this.transactionService.sum(query);

            query.setType(TransactionType.INCOME);
            final BigDecimal incomes = this.transactionService.sum(query);

            result.add(new TagAnalytic(
                    this.tagMapper.toTagDto(tag),
                    incomes,
                    expenses,
                    0L // TODO: think of a way to add this value without making a third query
            ));
        }

        return result;
    }

    @Override
    public List<TimeSeriesMetricDto> fetchMetricsByTime() {
        return List.of();
    }
}

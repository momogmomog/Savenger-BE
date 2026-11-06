package com.momo.savanger.api.analytics;

import com.momo.savanger.api.analytics.dto.AnalyticDto;
import com.momo.savanger.api.analytics.dto.AnalyticsSummaryDto;
import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricDto;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricQuery;
import com.momo.savanger.api.category.Category;
import com.momo.savanger.api.category.CategoryMapper;
import com.momo.savanger.api.category.CategoryService;
import com.momo.savanger.api.tag.Tag;
import com.momo.savanger.api.tag.TagMapper;
import com.momo.savanger.api.tag.TagService;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.dto.TransactionBasicDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.api.transaction.dto.TransactionSearchQueryForAnalytics;
import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
    public AnalyticsSummaryDto<CategoryAnalytic> fetchCategoryAnalytics(
            TransactionSearchQueryForAnalytics q) {
        final TransactionSearchQuery query = TransactionSearchQuery.fromAnalyticsQuery(q);

        final List<Long> categoryIds;
        if (!CollectionUtils.isEmpty(query.getCategoryIds())) {
            log.info("Using provided category IDs");
            categoryIds = query.getCategoryIds();
        } else {
            log.info("No category IDs provided, fetching based on transaction query.");
            categoryIds = this.transactionService.extractCategoryIds(query);
        }

        if (categoryIds.isEmpty()) {
            log.info("No category IDs found, aborting!");
            return AnalyticsSummaryDto.empty();
        }

        final List<Category> categories = this.categoryService.findAll(
                query.getBudgetId(),
                categoryIds
        );

        if (categories.isEmpty()) {
            log.info("No categories found, aborting!");
            return AnalyticsSummaryDto.empty();
        }

        final List<CategoryAnalytic> result = new ArrayList<>();

        for (Category category : categories) {
            query.setCategoryIds(List.of(category.getId()));
            query.setType(TransactionType.EXPENSE);
            final TransactionSumAndCount expenses = this.transactionService.sumAndCount(query);

            query.setType(TransactionType.INCOME);
            final TransactionSumAndCount incomes = this.transactionService.sumAndCount(query);

            result.add(new CategoryAnalytic(
                    this.categoryMapper.toCategoryDto(category),
                    incomes,
                    expenses
            ));
        }

        return this.buildSummary(result);
    }

    @Override
    public AnalyticsSummaryDto<TagAnalytic> fetchTagAnalytics(
            TransactionSearchQueryForAnalytics q) {
        final TransactionSearchQuery query = TransactionSearchQuery.fromAnalyticsQuery(q);
        final List<Long> tagIds;
        if (!CollectionUtils.isEmpty(query.getTagIds())) {
            log.info("Using provided tag IDs");
            tagIds = query.getTagIds();
        } else {
            log.info("No tag IDs provided, fetching based on transaction query.");
            tagIds = this.transactionService.extractTagIds(query);
        }

        if (tagIds.isEmpty()) {
            log.info("No tag IDs found, aborting!");
            return AnalyticsSummaryDto.empty();
        }

        final List<Tag> tags = this.tagService.findByBudgetAndIdContaining(
                tagIds,
                query.getBudgetId()
        );
        if (tags.isEmpty()) {
            log.info("No tags found, aborting!");
            return AnalyticsSummaryDto.empty();
        }

        final List<TagAnalytic> result = new ArrayList<>();
        for (Tag tag : tags) {
            query.setTagIds(List.of(tag.getId()));

            query.setType(TransactionType.EXPENSE);
            final TransactionSumAndCount expenses = this.transactionService.sumAndCount(query);

            query.setType(TransactionType.INCOME);
            final TransactionSumAndCount incomes = this.transactionService.sumAndCount(query);

            result.add(new TagAnalytic(
                    this.tagMapper.toTagDto(tag),
                    incomes,
                    expenses
            ));
        }

        return this.buildSummary(result);
    }

    @Override
    public List<TimeSeriesMetricDto> fetchMetricsByTime(TimeSeriesMetricQuery query) {
        return List.of();
    }

    private <T extends AnalyticDto> AnalyticsSummaryDto<T> buildSummary(List<T> items) {
        final List<TransactionBasicDto> distinctTransactions = items.stream()
                .flatMap(item -> Stream.of(item.getIncomes(), item.getExpenses()))
                .filter(Objects::nonNull)
                .map(TransactionSumAndCount::transactions)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .distinct()
                .toList();

        final BigDecimal incomesSum = distinctTransactions.stream()
                .filter(t -> t.type() == TransactionType.INCOME)
                .map(TransactionBasicDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal expensesSum = distinctTransactions.stream()
                .filter(t -> t.type() == TransactionType.EXPENSE)
                .map(TransactionBasicDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final BigDecimal total = incomesSum.subtract(expensesSum);

        final long incomesCount = distinctTransactions.stream()
                .filter(t -> t.type() == TransactionType.INCOME)
                .count();

        final long expensesCount = distinctTransactions.stream()
                .filter(t -> t.type() == TransactionType.EXPENSE)
                .count();

        final long totalCount = distinctTransactions.size();

        return new AnalyticsSummaryDto<>(
                incomesSum,
                expensesSum,
                total,
                incomesCount,
                expensesCount,
                totalCount,
                items
        );
    }
}

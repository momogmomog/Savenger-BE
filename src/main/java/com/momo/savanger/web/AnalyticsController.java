package com.momo.savanger.web;

import com.momo.savanger.api.analytics.AnalyticsService;
import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.constants.Endpoints;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping(Endpoints.ANALYTICS_CATEGORIES)
    public List<CategoryAnalytic> getCategoriesSpendingBreakdown(
            @Valid @RequestBody TransactionSearchQuery query) {
        return this.analyticsService.fetchCategoryAnalytics(query);
    }

    @PostMapping(Endpoints.ANALYTICS_TAGS)
    public List<TagAnalytic> getTagsSpendingBreakdown(
            @Valid @RequestBody TransactionSearchQuery query) {
        return this.analyticsService.fetchTagAnalytics(query);
    }
}

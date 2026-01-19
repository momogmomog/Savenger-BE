package com.momo.savanger.web;

import com.momo.savanger.api.analytics.AnalyticsService;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.constants.Endpoints;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@PreAuthorize("isFullyAuthenticated()")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @PostMapping(Endpoints.ANALYTICS_CATEGORIES)
    public void getCategoriesSpendingBreakdown(TransactionSearchQuery query) {

    }
}

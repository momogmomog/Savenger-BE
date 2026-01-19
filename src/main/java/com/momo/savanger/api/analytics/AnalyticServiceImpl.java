package com.momo.savanger.api.analytics;

import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricDto;
import com.momo.savanger.api.transaction.TransactionService;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticServiceImpl implements AnalyticsService {

    private final TransactionService transactionService;

    @Override
    public List<CategoryAnalytic> fetchCategoryAnalytics(TransactionSearchQuery query) {
        return List.of();
    }

    @Override
    public List<TagAnalytic> fetchTagAnalytics(TransactionSearchQuery query) {
        return List.of();
    }

    @Override
    public List<TimeSeriesMetricDto> fetchMetricsByTime() {
        return List.of();
    }
}

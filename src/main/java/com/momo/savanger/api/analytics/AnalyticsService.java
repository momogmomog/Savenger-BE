package com.momo.savanger.api.analytics;

import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQueryForAnalytics;
import java.util.List;

public interface AnalyticsService {

    List<CategoryAnalytic> fetchCategoryAnalytics(TransactionSearchQueryForAnalytics query);

    List<TagAnalytic> fetchTagAnalytics(TransactionSearchQueryForAnalytics query);

    List<TimeSeriesMetricDto> fetchMetricsByTime();
}

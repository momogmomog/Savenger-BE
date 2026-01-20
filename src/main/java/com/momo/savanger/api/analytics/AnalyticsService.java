package com.momo.savanger.api.analytics;

import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricDto;
import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import java.util.List;

public interface AnalyticsService {

    List<CategoryAnalytic> fetchCategoryAnalytics(TransactionSearchQuery query);

    List<TagAnalytic> fetchTagAnalytics(TransactionSearchQuery query);

    List<TimeSeriesMetricDto> fetchMetricsByTime();
}

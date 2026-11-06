package com.momo.savanger.api.analytics;

import com.momo.savanger.api.analytics.dto.AnalyticsSummaryDto;
import com.momo.savanger.api.analytics.dto.CategoryAnalytic;
import com.momo.savanger.api.analytics.dto.TagAnalytic;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricDto;
import com.momo.savanger.api.analytics.dto.TimeSeriesMetricQuery;
import com.momo.savanger.api.transaction.dto.TransactionSearchQueryForAnalytics;
import java.util.List;

public interface AnalyticsService {

    AnalyticsSummaryDto<CategoryAnalytic> fetchCategoryAnalytics(
            TransactionSearchQueryForAnalytics query);

    AnalyticsSummaryDto<TagAnalytic> fetchTagAnalytics(TransactionSearchQueryForAnalytics query);

    //TODO: Placeholder
    List<TimeSeriesMetricDto> fetchMetricsByTime(TimeSeriesMetricQuery query);
}

package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.analytics.AnalyticGranularity;
import com.momo.savanger.api.transaction.dto.TransactionSearchQueryForAnalytics;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TimeSeriesMetricQuery {

    @Valid
    @NotNull
    private TransactionSearchQueryForAnalytics query;

    private AnalyticGranularity granularity;
}

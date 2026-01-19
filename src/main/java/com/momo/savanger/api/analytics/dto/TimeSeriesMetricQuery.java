package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.transaction.dto.TransactionSearchQuery;
import com.momo.savanger.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Data;

@Data
public class TimeSeriesMetricQuery {

    @Valid
    @NotNull
    private TransactionSearchQuery query;


}

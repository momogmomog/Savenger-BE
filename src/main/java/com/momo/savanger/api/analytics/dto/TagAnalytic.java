package com.momo.savanger.api.analytics.dto;

import com.momo.savanger.api.tag.TagDto;
import com.momo.savanger.api.transaction.dto.TransactionSumAndCount;
import lombok.Data;

@Data
public class TagAnalytic {

    private final TagDto tag;

    private final TransactionSumAndCount incomes;
    private final TransactionSumAndCount expenses;
}

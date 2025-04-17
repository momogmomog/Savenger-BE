package com.momo.savanger.api.tag;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class TagDto {

    private Long id;

    private String tagName;

    private BigDecimal budgetCap;

    private Long budgetId;

}

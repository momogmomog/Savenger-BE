package com.momo.savanger.api.debt;

import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PayDebtDto {

    @NotNull
    @MinValueZero
    private BigDecimal amount;
}

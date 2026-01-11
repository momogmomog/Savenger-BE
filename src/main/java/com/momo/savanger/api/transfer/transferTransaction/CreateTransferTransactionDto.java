package com.momo.savanger.api.transfer.transferTransaction;

import com.momo.savanger.api.transfer.transferTransaction.constraints.ValidCreateTransferTransactionDto;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Data;

@Data
@ValidCreateTransferTransactionDto
public class CreateTransferTransactionDto {

    @NotNull
    private Long transferId;

    @NotNull
    @MinValueZero
    private BigDecimal amount;

    private String sourceComment;

    private String receiverComment;

    private Long sourceCategoryId;

    private Long receiverCategoryId;
}

package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.constraints.ValidTransactionDto;
import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.converter.DateTimeConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@ValidTransactionDto
public class EditTransactionDto implements ITransactionDto {

    private TransactionType type;

    @MinValueZero
    private BigDecimal amount;

    @DateTimeConverter
    private LocalDateTime dateCreated;

    @Length(max = Lengths.MAX_VARCHAR, message = ValidationMessages.TEXT_MUST_BE_BETWEEN)
    private String comment;

    private Long categoryId;

    @CanAccessBudget
    private Long budgetId;

    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return Objects.requireNonNullElse(this.tagIds, List.of());
    }
}

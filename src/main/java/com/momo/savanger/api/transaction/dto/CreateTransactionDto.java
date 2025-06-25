package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.budget.constraints.CanAccessBudget;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.transaction.constraints.ValidTransactionDto;
import com.momo.savanger.constants.Lengths;
import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.constraints.MinValueZero;
import com.momo.savanger.constraints.NotNull;
import com.momo.savanger.converter.DateTimeConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@ValidTransactionDto
public class CreateTransactionDto implements ITransactionDto {

    @NotNull
    private TransactionType type;

    @NotNull
    @MinValueZero
    private BigDecimal amount;

    @DateTimeConverter
    private LocalDateTime dateCreated;

    @Length(max = Lengths.MAX_VARCHAR, message = ValidationMessages.TEXT_MUST_BE_BETWEEN)
    private String comment;

    @NotNull
    private Long categoryId;

    @NotNull
    @CanAccessBudget
    private Long budgetId;

    private Long debtId;

    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return Objects.requireNonNullElse(this.tagIds, List.of());
    }

    public static CreateTransactionDto compensateDto(BigDecimal amount, Long budgetId) {
        final CreateTransactionDto dto = new CreateTransactionDto();
        dto.type = TransactionType.COMPENSATE;
        dto.amount = amount;
        dto.budgetId = budgetId;
        return dto;
    }

    public static CreateTransactionDto debtDto(BigDecimal amount, Long debtId, TransactionType type,
            Long budgetId) {

        final CreateTransactionDto createDto = new CreateTransactionDto();

        createDto.setAmount(amount);
        createDto.setBudgetId(budgetId);
        createDto.setType(type);
        createDto.setDebtId(debtId);

        return createDto;
    }
}

package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Data;

@Data
public class CreateTransactionServiceDto {

    private TransactionType type;

    private BigDecimal amount;

    private LocalDateTime dateCreated;

    private String comment;

    private Long categoryId;

    private Long budgetId;

    private Long debtId;

    private Long prepaymentId;

    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return Objects.requireNonNullElse(this.tagIds, List.of());
    }

    public static CreateTransactionServiceDto compensateDto(BigDecimal amount, Long budgetId) {
        final CreateTransactionServiceDto dto = new CreateTransactionServiceDto();
        dto.type = TransactionType.COMPENSATE;
        dto.amount = amount;
        dto.budgetId = budgetId;
        return dto;
    }

    public static CreateTransactionServiceDto debtDto(BigDecimal amount, Long debtId,
            TransactionType type,
            Long budgetId) {

        final CreateTransactionServiceDto createDto = new CreateTransactionServiceDto();

        createDto.setAmount(amount);
        createDto.setBudgetId(budgetId);
        createDto.setType(type);
        createDto.setDebtId(debtId);

        return createDto;
    }
}

package com.momo.savanger.api.budget.dto;

import com.momo.savanger.api.user.OtherUserDto;
import com.momo.savanger.converter.DateTimeConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class BudgetDto {

    private Long id;

    private String budgetName;

    private String recurringRule;

    @DateTimeConverter
    private LocalDateTime dateStarted;

    @DateTimeConverter
    private LocalDateTime dueDate;

    private Boolean active;

    private BigDecimal balance;

    private BigDecimal budgetCap;

    private Boolean autoRevise;

    private Long ownerId;

    private List<OtherUserDto> participants;
}

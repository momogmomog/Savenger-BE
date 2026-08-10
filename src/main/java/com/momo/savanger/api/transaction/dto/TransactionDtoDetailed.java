package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.category.CategoryDto;
import com.momo.savanger.api.tag.TagDto;
import com.momo.savanger.api.transaction.TransactionType;
import com.momo.savanger.api.user.OtherUserDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class TransactionDtoDetailed {

    private Long id;

    private TransactionType type;

    private BigDecimal amount;

    private LocalDateTime dateCreated;

    private String comment;

    private Boolean revised;

    private Long revisionId;

    private Long userId;

    private Long categoryId;

    private Long budgetId;

    private List<TagDto> tags;

    private CategoryDto category;

    private OtherUserDto user;
}

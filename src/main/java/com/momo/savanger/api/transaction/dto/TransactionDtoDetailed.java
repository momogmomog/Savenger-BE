package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.category.CategoryDto;
import com.momo.savanger.api.tag.TagDto;
import com.momo.savanger.api.user.UserDto;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionDtoDetailed extends TransactionDto {

    private List<TagDto> tags;

    private CategoryDto category;

    private UserDto user;
}

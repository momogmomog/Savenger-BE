package com.momo.savanger.api.transaction.dto;

import com.momo.savanger.api.tag.TagDto;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TransactionDtoDetailed extends TransactionDto {

    public List<TagDto> tags;
}

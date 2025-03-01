package com.momo.savanger.api.util;

import static com.momo.savanger.constants.ValidationMessages.FIELD_CANNOT_BE_NULL;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    @NotNull(message = FIELD_CANNOT_BE_NULL)
    @Min(0)
    private Integer pageNumber;

    @NotNull(message = FIELD_CANNOT_BE_NULL)
    @Min(1)
    private Integer pageSize;

    public PageRequest toPageRequest() {
        return PageRequest.of(this.pageNumber, this.pageSize);
    }
}

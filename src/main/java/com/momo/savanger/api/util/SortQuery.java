package com.momo.savanger.api.util;

import com.momo.savanger.constants.ValidationMessages;
import com.momo.savanger.converter.GenericEnumConverter;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortQuery {

    @NotEmpty(message = ValidationMessages.FIELD_CANNOT_BE_NULL)
    private String field;

    @GenericEnumConverter
    @NotNull(message = ValidationMessages.FIELD_IS_NULL_OR_INVALID)
    private SortDirection direction;
}

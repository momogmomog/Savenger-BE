package com.momo.savanger.error;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ConstratintViolationErrorResponse extends ErrorResponse {

    private final List<FieldErrorDto> fieldErrors;

    public ConstratintViolationErrorResponse(String path, List<FieldErrorDto> fieldErrors) {
        super(ApiErrorCode.ERR_0002, path);
        this.fieldErrors = fieldErrors;
    }
}

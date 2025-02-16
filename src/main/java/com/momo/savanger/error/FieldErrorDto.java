package com.momo.savanger.error;

import lombok.Data;

@Data
public class FieldErrorDto {

    private final String message;

    private final String field;

    private final Object rejectedValue;

    private final boolean bindingFailure;

    private final String constraintName;
}

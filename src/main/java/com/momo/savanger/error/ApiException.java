package com.momo.savanger.error;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ApiErrorCode errorCode;
    private final Object data;

    public ApiException(ApiErrorCode errorCode, Object data) {
        super(errorCode.message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public static ApiException with(ApiErrorCode errorCode) {
        return new ApiException(errorCode, null);
    }

    public static ApiException with(ApiErrorCode errorCode, Object data) {
        return new ApiException(errorCode, data);
    }
}

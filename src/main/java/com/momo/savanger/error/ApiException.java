package com.momo.savanger.error;

import java.util.Optional;
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

    public static Optional<ApiException> tryCatch(ApiErrorCode apiErrorCode, Runnable task) {
        try {
            task.run();
        } catch (ApiException exception) {
            if (apiErrorCode == exception.errorCode) {
                return Optional.of(exception);
            }

            throw exception;
        }

        return Optional.empty();
    }
}

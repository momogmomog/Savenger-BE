package com.momo.savanger.error;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@ToString
public class ErrorResponse {

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
    private final Object data;

    public ErrorResponse(ApiErrorCode apiErrorCode, String path, Object data) {
        status = apiErrorCode.status;
        errorCode = apiErrorCode.name();
        message = apiErrorCode.message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.data = data;
    }

    public ErrorResponse(ApiErrorCode apiErrorCode, String path) {
        this(apiErrorCode, path, null);
    }
}

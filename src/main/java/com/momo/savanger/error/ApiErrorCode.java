package com.momo.savanger.error;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ApiErrorCode {
    ERR_0001(HttpStatus.INTERNAL_SERVER_ERROR, "There was an error"),
    ERR_0002(HttpStatus.BAD_REQUEST, "Payload contains constraint violations"),
    ERR_0003(HttpStatus.UNAUTHORIZED, "Invalid Credentials"),
    ERR_0004(HttpStatus.NOT_FOUND, "Budget not found"),
    ;

    public final HttpStatus status;
    public final String message;

}

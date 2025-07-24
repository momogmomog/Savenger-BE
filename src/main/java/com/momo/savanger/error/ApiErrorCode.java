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
    ERR_0005(HttpStatus.NOT_FOUND, "Category not found"),
    ERR_0006(HttpStatus.NOT_FOUND, "Tag not found"),
    ERR_0007(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    ERR_0008(HttpStatus.BAD_REQUEST, "User is not logged in"),
    ERR_0009(HttpStatus.NOT_FOUND, "User not found"),
    ERR_0010(HttpStatus.NOT_FOUND, "Transaction not found"),
    ERR_0011(HttpStatus.BAD_REQUEST, "Error during request processing"),
    ERR_0012(HttpStatus.NOT_FOUND, "Revision not found"),
    ERR_0013(HttpStatus.NOT_FOUND, "Debt not found"),
    ERR_0014(HttpStatus.BAD_REQUEST, "Budget does not have enough money"),
    ERR_0015(HttpStatus.BAD_REQUEST, "You don't have permission to access these budgets"),
    ERR_0016(HttpStatus.NOT_FOUND, "Prepayment not found"),
    ERR_0017(HttpStatus.NOT_FOUND, "Recurring transaction not found");

    public final HttpStatus status;
    public final String message;

}

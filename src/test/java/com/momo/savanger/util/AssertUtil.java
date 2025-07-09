package com.momo.savanger.util;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.momo.savanger.error.ApiErrorCode;
import com.momo.savanger.error.ApiException;
import org.junit.jupiter.api.Assertions;

public final class AssertUtil {

    public static void assertApiException(ApiErrorCode expectedCode, Runnable runnable) {
        try {
            runnable.run();
            Assertions.fail(String.format("Exception with code %s was expected", expectedCode));
        } catch (ApiException exception) {
            assertThat(exception.getErrorCode()).isEqualTo(expectedCode);
        }
    }
}

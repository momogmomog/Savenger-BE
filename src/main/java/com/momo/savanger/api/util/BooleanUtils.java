package com.momo.savanger.api.util;

import java.util.Objects;

public final class BooleanUtils {

    public static boolean isTrue(Boolean b) {
        return Objects.requireNonNullElse(b, false);
    }
}

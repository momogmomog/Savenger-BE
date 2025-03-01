package com.momo.savanger.api.util;

import java.util.Objects;

public class StringUtil {

    public static String trimToNull(String str) {
        if (str == null) {
            return null;
        }

        final String res = str.trim();
        if (res.isEmpty()) {
            return null;
        }

        return res;
    }

    public static String trimToEmpty(String str) {
        return Objects.requireNonNullElse(trimToNull(str), "");
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }
}

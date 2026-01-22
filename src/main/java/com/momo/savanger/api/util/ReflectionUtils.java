package com.momo.savanger.api.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public final class ReflectionUtils {

    public static boolean fieldExists(Class<?> cls, String fieldName) {
        return Arrays.stream(cls.getDeclaredFields())
                .anyMatch(field -> field.getName().equalsIgnoreCase(fieldName));
    }

    public static boolean isNullOrEmpty(Object object) {
        switch (object) {
            case null -> {
                return true;
            }
            case Collection collection -> {
                return collection.isEmpty();
            }
            case Map map -> {
                return map.isEmpty();
            }
            case String s -> {
                return s.isEmpty();
            }
            default -> {
            }
        }

        final Class<?> clazz = object.getClass();
        if (clazz.isArray()) {
            return Array.getLength(object) == 0;
        }

        return false;
    }
}

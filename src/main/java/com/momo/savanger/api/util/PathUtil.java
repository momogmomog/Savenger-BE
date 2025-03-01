package com.momo.savanger.api.util;

public class PathUtil {

    public static String joinUrl(String url1, String url2) {
        url1 = StringUtil.trimToEmpty(url1);
        url2 = StringUtil.trimToEmpty(url2);

        if (url1.endsWith("/")) {
            url1 = url1.substring(0, url1.length() - 2);
        }

        if (url2.startsWith("/")) {
            url2 = url2.substring(1, url2.length() - 1);
        }

        return String.format("%s/%s", url1, url2);
    }
}

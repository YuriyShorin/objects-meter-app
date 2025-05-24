package ru.hse.objectsmeterapp.utils;

public class StringUtils {

    public static String stringOrDefault(String str, String defaultStr) {
        if (str == null || str.isEmpty() || str.isBlank()) {
            return defaultStr;
        }
        return str;
    }
}

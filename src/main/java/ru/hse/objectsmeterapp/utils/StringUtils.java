package ru.hse.objectsmeterapp.utils;

import java.util.List;

public class StringUtils {

    public static String clearBlanks(String line) {
        return line
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String clear(String line, String target) {
        return line
                .replace(target, "")
                .trim();
    }

    public static List<String> split(String line) {
        return List.of(line.split("\\s"));
    }
}

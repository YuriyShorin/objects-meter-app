package ru.hse.objectsmeterapp.utils;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ListUtils {

    public static <T, U> List<T> map(List<U> list, Function<U, T> comparator) {
        return list.stream()
                .map(comparator)
                .toList();
    }

    public static <T> T findMax(List<T> list, Comparator<T> comparator, T defaultValue) {
        return list.stream()
                .max(comparator)
                .orElse(defaultValue);
    }

    public static <T> T findMin(List<T> list, Comparator<T> comparator, T defaultValue) {
        return list.stream()
                .min(comparator)
                .orElse(defaultValue);
    }

    public static double[] listToArray(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}

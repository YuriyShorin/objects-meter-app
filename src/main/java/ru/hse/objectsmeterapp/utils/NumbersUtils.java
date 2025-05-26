package ru.hse.objectsmeterapp.utils;

import org.apache.commons.numbers.complex.Complex;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumbersUtils {

    public static Complex calculateDeterminant(Complex a, Complex b, Complex c, Complex d) {
        return a
                .multiply(d)
                .subtract(b
                        .multiply(c));
    }

    public static Double round(double value, int scale, RoundingMode roundingMode) {
        return BigDecimal.valueOf(value)
                .setScale(scale, roundingMode)
                .doubleValue();
    }

    public static Complex parseComplex(String real, String imaginary) {
        return Complex.ofCartesian(
                Double.parseDouble(real),
                Double.parseDouble(imaginary)
        );
    }

    public static Double countFrequency(Double frequency, String frequencyAbbreviation) {
        return switch (frequencyAbbreviation) {
            case "Hz" -> frequency;
            case "kHz" -> frequency / 1000;
            case "MHz" -> frequency / 1000000;
            default -> frequency / 1000000000;
        };
    }

    public static Double frequencyToHz(Double frequency, String frequencyAbbreviation) {
        return switch (frequencyAbbreviation) {
            case "Hz" -> frequency;
            case "kHz" -> frequency * 1000;
            case "MHz" -> frequency * 1000000;
            default -> frequency * 1000000000;
        };
    }
}

package ru.hse.objectsmeterapp.utils;

import org.apache.commons.numbers.complex.Complex;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumbersUtils {

    public static Complex fromPolarCoordinates(Double magnitudeInDb, Double phaseInDegrees) {
        double magnitude = Math.pow(10, magnitudeInDb / 20);
        double phaseInRadians = Math.toRadians(phaseInDegrees);
        double real = magnitude * Math.cos(phaseInRadians);
        double imaginary = magnitude * Math.sin(phaseInRadians);
        return Complex.ofCartesian(real, imaginary);
    }

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
}

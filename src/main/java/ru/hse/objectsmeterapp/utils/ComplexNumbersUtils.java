package ru.hse.objectsmeterapp.utils;

import org.apache.commons.numbers.complex.Complex;

public class ComplexNumbersUtils {

    public static Complex fromPolarCoordinates(Double magnitudeInDb, Double phaseInDegrees) {
        double magnitude = Math.pow(10, magnitudeInDb / 20);
        double phaseInRadians = Math.toRadians(phaseInDegrees);
        double real = magnitude * Math.cos(phaseInRadians);
        double imaginary = magnitude * Math.sin(phaseInRadians);
        return Complex.ofCartesian(real, imaginary);
    }
}

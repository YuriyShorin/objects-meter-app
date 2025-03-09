package ru.hse.objectsmeterapp.model;

import lombok.Data;
import org.apache.commons.numbers.complex.Complex;

@Data
public class MeasurementModel {

    private Double frequency;

    private Complex s11;

    private Complex s21;

    private Complex s12;

    private Complex s22;
}

package ru.hse.objectsmeterapp.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.numbers.complex.Complex;

@Data
@Builder
public class CalculatedModel {

    private Double frequency;

    private Complex s11L;

    private Complex s12L;

    private Complex s21L;

    private Complex s22L;

    private Complex s11T;

    private Complex s12T;

    private Complex s21T;

    private Complex s22T;

    private Complex sa11;

    private Complex sb11;

    private Complex sa12;

    private Complex sb12;

    private Complex sa21;

    private Complex sb21;

    private Complex sa22;

    private Complex sb22;

    private Complex s11x;

    private Complex s12x;

    private Complex s21x;

    private Complex s22x;
}

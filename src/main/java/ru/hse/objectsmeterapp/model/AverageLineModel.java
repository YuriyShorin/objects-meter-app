package ru.hse.objectsmeterapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.numbers.complex.Complex;

@Data
@AllArgsConstructor
public class AverageLineModel {

    private Double frequency;

    private Complex sa11;

    private Complex sb11;
}

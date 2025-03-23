package ru.hse.objectsmeterapp.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.numbers.complex.Complex;

@Data
@Builder
public class AverageLineModel {

    private Double frequency;

    private Complex sa11;

    private Complex sb11;
}

package ru.hse.objectsmeterapp.service;

import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.numbers.complex.Complex;
import ru.hse.objectsmeterapp.model.AverageLineModel;
import ru.hse.objectsmeterapp.model.CalculatedModel;
import ru.hse.objectsmeterapp.model.S2PFileModel;
import ru.hse.objectsmeterapp.utils.NumbersUtils;
import ru.hse.objectsmeterapp.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

public class CalculationService {

    private final static double PI = Math.PI;
    private final static double EPSILON = 0.01;

    public List<CalculatedModel> calculate(S2PFileModel lFileModel, S2PFileModel tFileModel, String frequencyAbbreviation) {
        List<CalculatedModel> calculatedModels = new ArrayList<>();
        List<AverageLineModel> frequencyPoints = findFrequencyPoints(lFileModel, tFileModel, frequencyAbbreviation);

        double[] frequencyArray = ListUtils
                .listToArray(ListUtils
                        .map(frequencyPoints,
                                AverageLineModel::getFrequency));
        double[] sa11RealArray = ListUtils
                .listToArray(ListUtils
                        .map(frequencyPoints,
                                averageLineModel -> averageLineModel.getSa11().getReal()));
        double[] sa11ImaginaryArray = ListUtils
                .listToArray(ListUtils
                        .map(frequencyPoints,
                                averageLineModel -> averageLineModel.getSa11().getImaginary()));
        double[] sb11RealArray = ListUtils
                .listToArray(ListUtils
                        .map(frequencyPoints,
                                averageLineModel -> averageLineModel.getSb11().getReal()));
        double[] sb11ImaginaryArray = ListUtils
                .listToArray(ListUtils
                        .map(frequencyPoints,
                                averageLineModel -> averageLineModel.getSb11().getImaginary()));

        AkimaSplineInterpolator akimaSplineInterpolator = new AkimaSplineInterpolator();
        PolynomialSplineFunction Sa11Real = akimaSplineInterpolator.interpolate(frequencyArray, sa11RealArray);
        PolynomialSplineFunction Sa11Imaginary = akimaSplineInterpolator.interpolate(frequencyArray, sa11ImaginaryArray);
        PolynomialSplineFunction Sb11Real = akimaSplineInterpolator.interpolate(frequencyArray, sb11RealArray);
        PolynomialSplineFunction Sb11Imaginary = akimaSplineInterpolator.interpolate(frequencyArray, sb11ImaginaryArray);

        for (int i = 0; i < lFileModel.getMeasurements().size(); i++) {
            Double frequency = countFrequency(lFileModel.getMeasurements().get(i).getFrequency(), frequencyAbbreviation);

            Complex s11L = lFileModel.getMeasurements().get(i).getS11();
            Complex s12L = lFileModel.getMeasurements().get(i).getS12();
            Complex s21L = lFileModel.getMeasurements().get(i).getS21();
            Complex s22L = lFileModel.getMeasurements().get(i).getS22();

            Complex s11T = tFileModel.getMeasurements().get(i).getS11();
            Complex s12T = tFileModel.getMeasurements().get(i).getS12();
            Complex s21T = tFileModel.getMeasurements().get(i).getS21();
            Complex s22T = tFileModel.getMeasurements().get(i).getS22();

            try {
                Complex sa11 = Complex.ofCartesian(Sa11Real.value(frequency), Sa11Imaginary.value(frequency));
                Complex sb11 = Complex.ofCartesian(Sb11Real.value(frequency), Sb11Imaginary.value(frequency));

                Complex sa22 = s22T
                        .subtract(sb11)
                        .divide(s12T);
                Complex sb22 = s11T
                        .subtract(sa11)
                        .divide(s21T);

                Complex sa1221 = Complex.ONE.
                        subtract(sa22
                                .multiply(sb22))
                        .multiply(s21T)
                        .sqrt();
                Complex sb1221 = Complex.ONE.
                        subtract(sa22
                                .multiply(sb22))
                        .multiply(s12T)
                        .sqrt();

                Complex detS = NumbersUtils.calculateDeterminant(s11L, s12L, s21L, s22L);
                Complex detSa = NumbersUtils.calculateDeterminant(sa11, sa1221, sa1221, sa22);
                Complex detSb = NumbersUtils.calculateDeterminant(sb11, sb1221, sb1221, sb22);

                Complex s11x = detS
                        .subtract(sa11
                                .multiply(s22L))
                        .multiply(sb22)
                        .subtract(s11L
                                .subtract(sa11)
                                .multiply(detSb))
                        .divide(sa22
                                .multiply(sb22
                                        .multiply(detS)
                                        .subtract(s11L
                                                .multiply(detSb)))
                                .add(detSb
                                        .subtract(s22L.
                                                multiply(sb22))
                                        .multiply(detSa)));
                Complex s21x = s21L
                        .multiply(sa1221)
                        .multiply(sb1221)
                        .divide(sa22
                                .multiply(sb22
                                        .multiply(detS)
                                        .subtract(s11L
                                                .multiply(detSb)))
                                .add(detSb
                                        .subtract(s22L
                                                .multiply(sb22))
                                        .multiply(detSa)));

                Complex s12x = s12L
                        .multiply(sa1221)
                        .multiply(sb1221)
                        .divide(sa22
                                .multiply(sb22
                                        .multiply(detS)
                                        .subtract(s11L
                                                .multiply(detSb)))
                                .add(detSb
                                        .subtract(s22L
                                                .multiply(sb22))
                                        .multiply(detSa)));

                Complex s22x = detS
                        .subtract(sb11
                                .multiply(s11L))
                        .multiply(sa22)
                        .subtract(s22L
                                .subtract(sb11)
                                .multiply(detSa))
                        .divide(sa22
                                .multiply(sb22
                                        .multiply(detS)
                                        .subtract(s11L
                                                .multiply(detSb)))
                                .add(detSb
                                        .subtract(s22L.
                                                multiply(sb22))
                                        .multiply(detSa)));

                CalculatedModel calculatedModel = CalculatedModel.builder()
                        .frequency(frequency)
                        .s11L(s11L)
                        .s12L(s12L)
                        .s21L(s21L)
                        .s22L(s22L)
                        .s11T(s11T)
                        .s12T(s12T)
                        .s21T(s21T)
                        .s22T(s22T)
                        .sa11(sa11)
                        .sb11(sb11)
                        .sa12(sa1221)
                        .sb12(sb1221)
                        .sa21(sa1221)
                        .sb21(sb1221)
                        .sa22(sa22)
                        .sb22(sb22)
                        .s11x(s11x)
                        .s12x(s12x)
                        .s21x(s21x)
                        .s22x(s22x)
                        .build();
                calculatedModels.add(calculatedModel);
            } catch (Exception ignored) {
            }
        }

        return calculatedModels;
    }

    private List<AverageLineModel> findFrequencyPoints(S2PFileModel lFileModel, S2PFileModel tFileModel, String frequencyAbbreviation) {
        List<AverageLineModel> frequencyPoints = new ArrayList<>();

        for (int i = 0; i < lFileModel.getMeasurements().size(); ++i) {
            Complex s11L = lFileModel.getMeasurements().get(i).getS11();
            Complex s21L = lFileModel.getMeasurements().get(i).getS21();
            Complex s12L = lFileModel.getMeasurements().get(i).getS12();
            Complex s22L = lFileModel.getMeasurements().get(i).getS22();

            Complex s11T = tFileModel.getMeasurements().get(i).getS11();
            Complex s21T = tFileModel.getMeasurements().get(i).getS21();
            Complex s12T = tFileModel.getMeasurements().get(i).getS12();
            Complex s22T = tFileModel.getMeasurements().get(i).getS22();

            double arg = s21L
                    .divide(s21T)
                    .arg();

            if (Math.abs(arg - PI / 2) <= EPSILON || Math.abs(arg + PI / 2) <= EPSILON) {
                double frequency = countFrequency(lFileModel.getMeasurements().get(i).getFrequency(), frequencyAbbreviation);

                Complex t = s21L.divide(s21T);
                Complex sA11 = s11L
                        .multiply(s12T)
                        .subtract(s11T
                                .multiply(s12L)
                                .multiply(t))
                        .divide(s12T
                                .subtract(s12L
                                        .multiply(t)));
                Complex sB11 = s22L
                        .multiply(s21T)
                        .subtract(s22T
                                .multiply(s21L)
                                .multiply(t))
                        .divide(s21T.
                                subtract(s21L
                                        .multiply(t)));

                AverageLineModel averageLine = AverageLineModel.builder()
                        .frequency(frequency)
                        .sa11(sA11)
                        .sb11(sB11)
                        .build();
                frequencyPoints.add(averageLine);
            }
        }

        return frequencyPoints;
    }

    private Double countFrequency(Double frequency, String frequencyAbbreviation) {
        return switch (frequencyAbbreviation) {
            case "Hz" -> frequency;
            case "kHz" -> frequency / 1000;
            case "MHz" -> frequency / 1000000;
            default -> frequency / 1000000000;
        };
    }
}

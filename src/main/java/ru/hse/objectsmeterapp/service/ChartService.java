package ru.hse.objectsmeterapp.service;

import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.numbers.complex.Complex;
import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.model.AverageLineModel;
import ru.hse.objectsmeterapp.model.S2PFileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ChartService {

    private final static int NUMBER_OF_CHARTS = 16;

    private final static double PI = Math.PI;

    private final static double EPSILON = 0.01;

    public void createViewerCharts(List<LineChart<Number, Number>> charts,
                                   Map<String, S2PFileModel> s2PFileModelsByFileNames,
                                   String frequencyAbbreviation) {
        clearCharts(charts);

        int seriesNumber = NUMBER_OF_CHARTS * s2PFileModelsByFileNames.size();
        List<XYChart.Series<Number, Number>> series = createSeries(seriesNumber);
        List<String> fileNames = new ArrayList<>();
        final AtomicInteger seriesCounter = new AtomicInteger(0);

        s2PFileModelsByFileNames.forEach((fileName, s2PFileModel) -> {
            fileNames.add(fileName);
            s2PFileModel.getMeasurements().forEach(measurementModel -> {
                Double frequency = countFrequency(measurementModel.getFrequency(), frequencyAbbreviation);

                series.get(seriesCounter.get()).getData().add(new XYChart.Data<>(frequency, measurementModel.getS11().getReal()));
                series.get(seriesCounter.get() + 1).getData().add(new XYChart.Data<>(frequency, measurementModel.getS11().getImaginary()));
                series.get(seriesCounter.get() + 2).getData().add(new XYChart.Data<>(frequency, measurementModel.getS11().abs()));
                series.get(seriesCounter.get() + 3).getData().add(new XYChart.Data<>(frequency, measurementModel.getS11().arg()));

                series.get(seriesCounter.get() + 4).getData().add(new XYChart.Data<>(frequency, measurementModel.getS21().getReal()));
                series.get(seriesCounter.get() + 5).getData().add(new XYChart.Data<>(frequency, measurementModel.getS21().getImaginary()));
                series.get(seriesCounter.get() + 6).getData().add(new XYChart.Data<>(frequency, measurementModel.getS21().abs()));
                series.get(seriesCounter.get() + 7).getData().add(new XYChart.Data<>(frequency, measurementModel.getS21().arg()));

                series.get(seriesCounter.get() + 8).getData().add(new XYChart.Data<>(frequency, measurementModel.getS12().getReal()));
                series.get(seriesCounter.get() + 9).getData().add(new XYChart.Data<>(frequency, measurementModel.getS12().getImaginary()));
                series.get(seriesCounter.get() + 10).getData().add(new XYChart.Data<>(frequency, measurementModel.getS12().abs()));
                series.get(seriesCounter.get() + 11).getData().add(new XYChart.Data<>(frequency, measurementModel.getS12().arg()));

                series.get(seriesCounter.get() + 12).getData().add(new XYChart.Data<>(frequency, measurementModel.getS22().getReal()));
                series.get(seriesCounter.get() + 13).getData().add(new XYChart.Data<>(frequency, measurementModel.getS22().getImaginary()));
                series.get(seriesCounter.get() + 14).getData().add(new XYChart.Data<>(frequency, measurementModel.getS22().abs()));
                series.get(seriesCounter.get() + 15).getData().add(new XYChart.Data<>(frequency, measurementModel.getS22().arg()));
            });

            seriesCounter.set(seriesCounter.get() + NUMBER_OF_CHARTS);
        });

        fulfilCharts(charts, series, fileNames);
    }

    public void createLTCharts(List<LineChart<Number, Number>> charts, List<ScatterChart<Number, Number>> frequencyCharts,
                               S2PFileModel lFileModel, S2PFileModel tFileModel, String frequencyAbbreviation) {
        clearCharts(charts);

        List<XYChart.Series<Number, Number>> frequencySeries = createSeries(4);
        List<AverageLineModel> frequencyPoints = findFrequencyPoints(lFileModel, tFileModel, frequencyAbbreviation);

        frequencyPoints.forEach(frequencyPoint -> {
                    frequencySeries.getFirst().getData().add(new XYChart.Data<>(frequencyPoint.getFrequency(), frequencyPoint.getSa11().getReal()));
                    frequencySeries.get(1).getData().add(new XYChart.Data<>(frequencyPoint.getFrequency(), frequencyPoint.getSa11().getImaginary()));
                    frequencySeries.get(2).getData().add(new XYChart.Data<>(frequencyPoint.getFrequency(), frequencyPoint.getSb11().getReal()));
                    frequencySeries.get(3).getData().add(new XYChart.Data<>(frequencyPoint.getFrequency(), frequencyPoint.getSb11().getImaginary()));
                }
        );

        frequencyCharts.getFirst().getData().add(frequencySeries.getFirst());
        frequencyCharts.get(1).getData().add(frequencySeries.get(1));
        frequencyCharts.get(2).getData().add(frequencySeries.get(2));
        frequencyCharts.get(3).getData().add(frequencySeries.get(3));

        List<XYChart.Series<Number, Number>> series = createSeries(12);

        double[] frequencyArray = listToArray(frequencyPoints.stream()
                .map(AverageLineModel::getFrequency)
                .toList());
        double[] sa11RealArray = listToArray(frequencyPoints.stream()
                .map(averageLineModel -> averageLineModel.getSa11().getReal())
                .toList());
        double[] sa11ImaginaryArray = listToArray(frequencyPoints.stream()
                .map(averageLineModel -> averageLineModel.getSa11().getImaginary())
                .toList());
        double[] sb11RealArray = listToArray(frequencyPoints.stream()
                .map(averageLineModel -> averageLineModel.getSb11().getReal())
                .toList());
        double[] sb11ImaginaryArray = listToArray(frequencyPoints.stream()
                .map(averageLineModel -> averageLineModel.getSb11().getImaginary())
                .toList());

        AkimaSplineInterpolator akimaSplineInterpolator = new AkimaSplineInterpolator();
        PolynomialSplineFunction akimaSa11Real = akimaSplineInterpolator.interpolate(frequencyArray, sa11RealArray);
        PolynomialSplineFunction akimaSa11Imaginary = akimaSplineInterpolator.interpolate(frequencyArray, sa11ImaginaryArray);
        PolynomialSplineFunction akimaSb11Real = akimaSplineInterpolator.interpolate(frequencyArray, sb11RealArray);
        PolynomialSplineFunction akimaSb11Imaginary = akimaSplineInterpolator.interpolate(frequencyArray, sb11ImaginaryArray);

        for (int i = 0; i < lFileModel.getMeasurements().size(); i++) {
            System.out.println("Loading " + i + "/" + lFileModel.getMeasurements().size());

            Double frequency = countFrequency(lFileModel.getMeasurements().get(i).getFrequency(), frequencyAbbreviation);

            Complex s11L = lFileModel.getMeasurements().get(i).getS11();
            Complex s22L = lFileModel.getMeasurements().get(i).getS22();

            Complex s11T = tFileModel.getMeasurements().get(i).getS11();
            Complex s12T = tFileModel.getMeasurements().get(i).getS12();
            Complex s21T = tFileModel.getMeasurements().get(i).getS21();
            Complex s22T = tFileModel.getMeasurements().get(i).getS22();

            try {
                Complex sa11Akima = Complex.ofCartesian(akimaSa11Real.value(frequency), akimaSa11Imaginary.value(frequency));
                Complex sb11Akima = Complex.ofCartesian(akimaSb11Real.value(frequency), akimaSb11Imaginary.value(frequency));

                Complex sa22Akima = s22T
                        .subtract(sb11Akima)
                        .divide(s12T);
                Complex sb22Akima = s11T
                        .subtract(sa11Akima)
                        .divide(s21T);

                series.getFirst().getData().add(new XYChart.Data<>(frequency, s11L.getReal()));
                series.get(1).getData().add(new XYChart.Data<>(frequency, sa11Akima.getReal()));

                series.get(2).getData().add(new XYChart.Data<>(frequency, s11L.getImaginary()));
                series.get(3).getData().add(new XYChart.Data<>(frequency, sa11Akima.getImaginary()));

                series.get(4).getData().add(new XYChart.Data<>(frequency, s22L.getReal()));
                series.get(5).getData().add(new XYChart.Data<>(frequency, sb11Akima.getReal()));

                series.get(6).getData().add(new XYChart.Data<>(frequency, s22L.getImaginary()));
                series.get(7).getData().add(new XYChart.Data<>(frequency, sb11Akima.getImaginary()));

                series.get(8).getData().add(new XYChart.Data<>(frequency, sa22Akima.getReal()));
                series.get(9).getData().add(new XYChart.Data<>(frequency, sa22Akima.getImaginary()));

                series.get(10).getData().add(new XYChart.Data<>(frequency, sb22Akima.getReal()));
                series.get(11).getData().add(new XYChart.Data<>(frequency, sb22Akima.getImaginary()));
            } catch (Exception ignored) {

            }
        }

        charts.getFirst().getData().add(series.getFirst());
        charts.getFirst().getData().add(series.get(1));

        charts.get(1).getData().add(series.get(2));
        charts.get(1).getData().add(series.get(3));

        charts.get(2).getData().add(series.get(4));
        charts.get(2).getData().add(series.get(5));

        charts.get(3).getData().add(series.get(6));
        charts.get(3).getData().add(series.get(7));

        charts.get(4).getData().add(series.get(8));
        charts.get(5).getData().add(series.get(9));
        charts.get(6).getData().add(series.get(10));
        charts.get(7).getData().add(series.get(11));
    }

    public void setXAxisAutoRanging(List<LineChart<Number, Number>> charts) {
        charts.forEach(chart -> {
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            xAxis.setAutoRanging(true);
        });
    }

    public void setYAxisAutoRanging(List<LineChart<Number, Number>> charts) {
        charts.forEach(chart -> {
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();
            yAxis.setAutoRanging(true);
        });
    }

    public void setXAxisRanging(List<LineChart<Number, Number>> charts, Double lowerBound, Double upperBound) throws BusinessException {
        if (lowerBound >= upperBound) {
            throw new BusinessException("Минимальное значение оси Х больше или равно максимальному");
        }

        charts.forEach(chart -> {
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(lowerBound);
            xAxis.setUpperBound(upperBound);
        });
    }

    public void setYAxisRanging(List<LineChart<Number, Number>> charts, Double lowerBound, Double upperBound) throws BusinessException {
        if (lowerBound >= upperBound) {
            throw new BusinessException("Минимальное значение оси Y больше или равно максимальному");
        }

        charts.forEach(chart -> {
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(lowerBound);
            yAxis.setUpperBound(upperBound);
        });
    }

    public void clearCharts(List<LineChart<Number, Number>> charts) {
        charts.forEach(chart -> chart.getData().clear());
    }

    private List<XYChart.Series<Number, Number>> createSeries(int seriesNumber) {
        List<XYChart.Series<Number, Number>> series = new ArrayList<>();
        for (int i = 0; i < seriesNumber; i++) {
            series.add(new XYChart.Series<>());
        }
        return series;
    }

    private void fulfilCharts(List<LineChart<Number, Number>> charts, List<XYChart.Series<Number, Number>> series, List<String> fileNames) {
        final AtomicInteger seriesCount = new AtomicInteger(0);
        fileNames.forEach(fileName -> charts.forEach(chart -> {
            series.get(seriesCount.get()).setName(fileName);
            chart.getData().add(series.get(seriesCount.get()));
            seriesCount.getAndIncrement();
        }));
    }

    private Double countFrequency(Double frequency, String frequencyAbbreviation) {
        return switch (frequencyAbbreviation) {
            case "Hz" -> frequency;
            case "kHz" -> frequency / 1000;
            case "MHz" -> frequency / 1000000;
            default -> frequency / 1000000000;
        };
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

                frequencyPoints.add(new AverageLineModel(frequency, sA11, sB11));
            }
        }

        return frequencyPoints;
    }

    private double[] listToArray(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}

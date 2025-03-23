package ru.hse.objectsmeterapp.service;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.model.CalculatedModel;
import ru.hse.objectsmeterapp.utils.ListUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChartService {

    private final static int NUMBER_OF_SERIES = 20;

    public void createCharts(List<LineChart<Number, Number>> charts, List<CalculatedModel> calculatedModels) {
        List<XYChart.Series<Number, Number>> series = createSeries();

        calculatedModels.forEach(calculatedModel -> {
            Double frequency = calculatedModel.getFrequency();

            // s11 charts
            series.getFirst().getData().add(new XYChart.Data<>(frequency, calculatedModel.getS11L().getReal()));
            series.get(1).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa11().getReal()));

            series.get(2).getData().add(new XYChart.Data<>(frequency, calculatedModel.getS11L().getImaginary()));
            series.get(3).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa11().getImaginary()));

            series.get(4).getData().add(new XYChart.Data<>(frequency, calculatedModel.getS22L().getReal()));
            series.get(5).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb11().getReal()));

            series.get(6).getData().add(new XYChart.Data<>(frequency, calculatedModel.getS22L().getImaginary()));
            series.get(7).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb11().getImaginary()));

            // s12 charts
            series.get(8).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa12().getReal()));
            series.get(9).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa12().getImaginary()));

            series.get(10).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb12().getReal()));
            series.get(11).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb12().getImaginary()));

            // s21 charts
            series.get(12).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa21().getReal()));
            series.get(13).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa21().getImaginary()));

            series.get(14).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb21().getReal()));
            series.get(15).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb21().getImaginary()));

            // s22 charts
            series.get(16).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa22().getReal()));
            series.get(17).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSa22().getImaginary()));

            series.get(18).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb22().getReal()));
            series.get(19).getData().add(new XYChart.Data<>(frequency, calculatedModel.getSb22().getImaginary()));
        });

        setXAxisAutoRanging(charts, calculatedModels);

        // s11 charts
        charts.getFirst().getData().add(series.getFirst());
        charts.getFirst().getData().add(series.get(1));

        charts.get(1).getData().add(series.get(2));
        charts.get(1).getData().add(series.get(3));

        charts.get(2).getData().add(series.get(4));
        charts.get(2).getData().add(series.get(5));

        charts.get(3).getData().add(series.get(6));
        charts.get(3).getData().add(series.get(7));

        // s12 charts
        charts.get(4).getData().add(series.get(8));
        charts.get(5).getData().add(series.get(9));
        charts.get(6).getData().add(series.get(10));
        charts.get(7).getData().add(series.get(11));

        // s21 charts
        charts.get(8).getData().add(series.get(12));
        charts.get(9).getData().add(series.get(13));
        charts.get(10).getData().add(series.get(14));
        charts.get(11).getData().add(series.get(15));

        // s22 charts
        charts.get(12).getData().add(series.get(16));
        charts.get(13).getData().add(series.get(17));
        charts.get(14).getData().add(series.get(18));
        charts.get(15).getData().add(series.get(19));
    }

    public void setXAxisAutoRanging(List<LineChart<Number, Number>> charts, List<CalculatedModel> calculatedModels) {
        Double minFrequency = ListUtils
                .findMin(ListUtils
                                .map(calculatedModels,
                                        CalculatedModel::getFrequency),
                        Comparator
                                .comparingDouble(Double::doubleValue),
                        0.0);
        Double maxFrequency = ListUtils
                .findMax(ListUtils
                                .map(calculatedModels,
                                        CalculatedModel::getFrequency),
                        Comparator
                                .comparingDouble(Double::doubleValue),
                        20.0);

        charts.forEach(chart -> {
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            xAxis.setAutoRanging(false);
            xAxis.setLowerBound(minFrequency);
            xAxis.setUpperBound(maxFrequency);
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

    private List<XYChart.Series<Number, Number>> createSeries() {
        List<XYChart.Series<Number, Number>> series = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_SERIES; i++) {
            series.add(new XYChart.Series<>());
        }
        return series;
    }
}

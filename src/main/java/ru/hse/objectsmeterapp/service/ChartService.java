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
        List<List<XYChart.Data<Number, Number>>> dataList = createDataList(calculatedModels);

        for (int i = 0; i < NUMBER_OF_SERIES; i++) {
            series.get(i).getData().addAll(dataList.get(i));
        }

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

    private List<List<XYChart.Data<Number, Number>>> createDataList(List<CalculatedModel> calculatedModels) {
        List<List<XYChart.Data<Number, Number>>> dataList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_SERIES; i++) {
            dataList.add(new ArrayList<>());
        }

        calculatedModels.forEach(calculatedModel -> {
            Double frequency = calculatedModel.getFrequency();

            // s11 charts
            dataList.getFirst().add(new XYChart.Data<>(frequency, calculatedModel.getS11L().getReal()));
            dataList.get(1).add(new XYChart.Data<>(frequency, calculatedModel.getSa11().getReal()));

            dataList.get(2).add(new XYChart.Data<>(frequency, calculatedModel.getS11L().getImaginary()));
            dataList.get(3).add(new XYChart.Data<>(frequency, calculatedModel.getSa11().getImaginary()));

            dataList.get(4).add(new XYChart.Data<>(frequency, calculatedModel.getS22L().getReal()));
            dataList.get(5).add(new XYChart.Data<>(frequency, calculatedModel.getSb11().getReal()));

            dataList.get(6).add(new XYChart.Data<>(frequency, calculatedModel.getS22L().getImaginary()));
            dataList.get(7).add(new XYChart.Data<>(frequency, calculatedModel.getSb11().getImaginary()));

            // s12 charts
            dataList.get(8).add(new XYChart.Data<>(frequency, calculatedModel.getSa12().getReal()));
            dataList.get(9).add(new XYChart.Data<>(frequency, calculatedModel.getSa12().getImaginary()));

            dataList.get(10).add(new XYChart.Data<>(frequency, calculatedModel.getSb12().getReal()));
            dataList.get(11).add(new XYChart.Data<>(frequency, calculatedModel.getSb12().getImaginary()));

            // s21 charts
            dataList.get(12).add(new XYChart.Data<>(frequency, calculatedModel.getSa21().getReal()));
            dataList.get(13).add(new XYChart.Data<>(frequency, calculatedModel.getSa21().getImaginary()));

            dataList.get(14).add(new XYChart.Data<>(frequency, calculatedModel.getSb21().getReal()));
            dataList.get(15).add(new XYChart.Data<>(frequency, calculatedModel.getSb21().getImaginary()));

            // s22 charts
            dataList.get(16).add(new XYChart.Data<>(frequency, calculatedModel.getSa22().getReal()));
            dataList.get(17).add(new XYChart.Data<>(frequency, calculatedModel.getSa22().getImaginary()));

            dataList.get(18).add(new XYChart.Data<>(frequency, calculatedModel.getSb22().getReal()));
            dataList.get(19).add(new XYChart.Data<>(frequency, calculatedModel.getSb22().getImaginary()));
        });

        return dataList;
    }
}

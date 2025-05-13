package ru.hse.objectsmeterapp.service;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.model.CalculatedModel;
import ru.hse.objectsmeterapp.utils.ListUtils;
import ru.hse.objectsmeterapp.utils.NumbersUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ChartService {

    private final static int NUMBER_OF_SERIES = 28;

    private double mouseAnchorX = 0.0;
    private double mouseAnchorY = 0.0;
    private double xLowerBound = 0.0;
    private double xUpperBound = 0.0;
    private double yLowerBound = 0.0;
    private double yUpperBound = 0.0;

    public void addZoomForCharts(List<LineChart<Number, Number>> charts) {
        charts.forEach(chart -> {
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            NumberAxis yAxis = (NumberAxis) chart.getYAxis();

            chart.setOnScroll((ScrollEvent event) -> {
                double zoomFactor = event.getDeltaY() > 0 ? 0.9 : 1.1;

                double xCenter = (xAxis.getLowerBound() + xAxis.getUpperBound()) / 2;
                double xLength = (xAxis.getUpperBound() - xAxis.getLowerBound()) * zoomFactor;
                xAxis.setLowerBound(xCenter - xLength / 2);
                xAxis.setUpperBound(xCenter + xLength / 2);

                double yCenter = (yAxis.getLowerBound() + yAxis.getUpperBound()) / 2;
                double yLength = (yAxis.getUpperBound() - yAxis.getLowerBound()) * zoomFactor;
                yAxis.setAutoRanging(false);
                yAxis.setLowerBound(yCenter - yLength / 2);
                yAxis.setUpperBound(yCenter + yLength / 2);
            });

            chart.setOnMousePressed((MouseEvent event) -> {
                mouseAnchorX = event.getX();
                mouseAnchorY = event.getY();
                xLowerBound = xAxis.getLowerBound();
                xUpperBound = xAxis.getUpperBound();
                yLowerBound = yAxis.getLowerBound();
                yUpperBound = yAxis.getUpperBound();
            });

            chart.setOnMouseDragged((MouseEvent event) -> {
                double deltaX = (event.getX() - mouseAnchorX) / chart.getWidth();
                double deltaY = (event.getY() - mouseAnchorY) / chart.getHeight();

                double xShift = deltaX * (xUpperBound - xLowerBound);
                double yShift = deltaY * (yUpperBound - yLowerBound);

                xAxis.setLowerBound(xLowerBound - xShift);
                xAxis.setUpperBound(xUpperBound - xShift);
                yAxis.setAutoRanging(false);
                yAxis.setLowerBound(yLowerBound + yShift);
                yAxis.setUpperBound(yUpperBound + yShift);
            });
        });
    }

    public void createCharts(List<LineChart<Number, Number>> charts, List<CalculatedModel> calculatedModels) {
        List<XYChart.Series<Number, Number>> series = createSeries();
        List<List<XYChart.Data<Number, Number>>> dataList = createDataList(calculatedModels);

        addDataListToSeries(series, dataList);
        setXAxisAutoRanging(charts, calculatedModels);
        addSeriesToCharts(charts, series);
        setLegendsNotVisible(charts);
    }

    public void setXAxisAutoRanging(List<LineChart<Number, Number>> charts, List<CalculatedModel> calculatedModels) {
        Double minFrequency = NumbersUtils.round(ListUtils
                .findMin(ListUtils
                                .map(calculatedModels,
                                        CalculatedModel::getFrequency),
                        Comparator
                                .comparingDouble(Double::doubleValue),
                        0.0), 0, RoundingMode.DOWN);
        Double maxFrequency = NumbersUtils.round(ListUtils
                .findMax(ListUtils
                                .map(calculatedModels,
                                        CalculatedModel::getFrequency),
                        Comparator
                                .comparingDouble(Double::doubleValue),
                        20.0), 0, RoundingMode.UP);

        charts.forEach(chart -> {
            NumberAxis xAxis = (NumberAxis) chart.getXAxis();
            xAxis.setAutoRanging(false);
            xAxis.setTickUnit((maxFrequency - minFrequency) / 20.0);
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

            // s11x charts
            dataList.get(20).add(new XYChart.Data<>(frequency, calculatedModel.getS11x().getReal()));
            dataList.get(21).add(new XYChart.Data<>(frequency, calculatedModel.getS11x().getImaginary()));

            // s21x charts
            dataList.get(22).add(new XYChart.Data<>(frequency, calculatedModel.getS21x().getReal()));
            dataList.get(23).add(new XYChart.Data<>(frequency, calculatedModel.getS21x().getImaginary()));

            // s12x charts
            dataList.get(24).add(new XYChart.Data<>(frequency, calculatedModel.getS12x().getReal()));
            dataList.get(25).add(new XYChart.Data<>(frequency, calculatedModel.getS12x().getImaginary()));

            // s22x charts
            dataList.get(26).add(new XYChart.Data<>(frequency, calculatedModel.getS22x().getReal()));
            dataList.get(27).add(new XYChart.Data<>(frequency, calculatedModel.getS22x().getImaginary()));
        });

        return dataList;
    }

    public void addDataListToSeries(List<XYChart.Series<Number, Number>> series, List<List<XYChart.Data<Number, Number>>> dataList) {
        for (int i = 0; i < NUMBER_OF_SERIES; i++) {
            series.get(i).getData().addAll(dataList.get(i));
        }
    }

    private void addSeriesToCharts(List<LineChart<Number, Number>> charts, List<XYChart.Series<Number, Number>> series) {
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

        // s11x charts
        charts.get(16).getData().add(series.get(20));
        charts.get(17).getData().add(series.get(21));

        // s21x charts
        charts.get(18).getData().add(series.get(22));
        charts.get(19).getData().add(series.get(23));

        // s12x charts
        charts.get(20).getData().add(series.get(24));
        charts.get(21).getData().add(series.get(25));

        // s22x charts
        charts.get(22).getData().add(series.get(26));
        charts.get(23).getData().add(series.get(27));
    }

    private void setLegendsNotVisible(List<LineChart<Number, Number>> charts) {
        charts.forEach(chart -> chart.setLegendVisible(false));
    }
}

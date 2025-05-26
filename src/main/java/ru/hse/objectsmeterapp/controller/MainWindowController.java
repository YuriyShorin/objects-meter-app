package ru.hse.objectsmeterapp.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ru.hse.objectsmeterapp.model.enums.FrequencyAbbreviations;
import ru.hse.objectsmeterapp.model.CalculatedModel;
import ru.hse.objectsmeterapp.model.MeasurementModel;
import ru.hse.objectsmeterapp.service.CalculationService;
import ru.hse.objectsmeterapp.service.ChartService;
import ru.hse.objectsmeterapp.service.FileService;
import ru.hse.objectsmeterapp.service.MicranConnectService;
import ru.hse.objectsmeterapp.service.MicranMeasurementsService;
import ru.hse.objectsmeterapp.utils.ChangeTransformer;
import ru.hse.objectsmeterapp.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainWindowController {

    private static final String DEFAULT_START_FREQUENCY = "10000000.0";
    private static final String DEFAULT_STOP_FREQUENCY = "20000000000.0";
    private static final String DEFAULT_POINTS = "500";

    private final FileService fileService;

    private final CalculationService calculationService;

    private final ChartService chartService;

    private final MicranConnectService micranConnectService;

    private final MicranMeasurementsService micranMeasurementsService;

    private final List<LineChart<Number, Number>> charts;

    private final List<List<MeasurementModel>> micranMeasurements;

    private List<CalculatedModel> calculatedModels;

    @FXML
    private CheckBox xAutoCheckBox;

    @FXML
    private CheckBox yAutoCheckBox;

    @FXML
    private TextField xMinTextField;

    @FXML
    private TextField xMaxTextField;

    @FXML
    private TextField yMinTextField;

    @FXML
    private TextField yMaxTextField;

    @FXML
    private ComboBox<String> frequencyComboBox;

    @FXML
    private Circle connectionIndicator;

    @FXML
    private Label deviceIdLabel;

    @FXML
    private Label measurementsCounterLabel;

    @FXML
    private Label startFrequencyLabel;

    @FXML
    private Label stopFrequencyLabel;

    @FXML
    private TextField startFrequencyTextField;

    @FXML
    private TextField stopFrequencyTextField;

    @FXML
    private TextField pointsTextField;

    @FXML
    private LineChart<Number, Number> sa11RealChart;

    @FXML
    private LineChart<Number, Number> sa11ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sb11RealChart;

    @FXML
    private LineChart<Number, Number> sb11ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sa12RealChart;

    @FXML
    private LineChart<Number, Number> sa12ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sb12RealChart;

    @FXML
    private LineChart<Number, Number> sb12ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sa21RealChart;

    @FXML
    private LineChart<Number, Number> sa21ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sb21RealChart;

    @FXML
    private LineChart<Number, Number> sb21ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sa22RealChart;

    @FXML
    private LineChart<Number, Number> sa22ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sb22RealChart;

    @FXML
    private LineChart<Number, Number> sb22ImaginaryChart;

    @FXML
    private LineChart<Number, Number> s11xRealChart;

    @FXML
    private LineChart<Number, Number> s11xImaginaryChart;

    @FXML
    private LineChart<Number, Number> s21xRealChart;

    @FXML
    private LineChart<Number, Number> s21xImaginaryChart;

    @FXML
    private LineChart<Number, Number> s12xRealChart;

    @FXML
    private LineChart<Number, Number> s12xImaginaryChart;

    @FXML
    private LineChart<Number, Number> s22xRealChart;

    @FXML
    private LineChart<Number, Number> s22xImaginaryChart;

    public MainWindowController() {
        this.fileService = new FileService();
        this.chartService = new ChartService();
        this.calculationService = new CalculationService();
        this.micranConnectService = new MicranConnectService();
        this.micranMeasurementsService = new MicranMeasurementsService();

        this.charts = new ArrayList<>();
        this.calculatedModels = new ArrayList<>();
        this.micranMeasurements = new ArrayList<>();
    }

    public void initialize() {
        addTextFormatters();
        initializeFrequencyComboBox();

        charts.addAll(List.of(sa11RealChart, sa11ImaginaryChart, sb11RealChart, sb11ImaginaryChart,
                sa12RealChart, sa12ImaginaryChart, sb12RealChart, sb12ImaginaryChart,
                sa21RealChart, sa21ImaginaryChart, sb21RealChart, sb21ImaginaryChart,
                sa22RealChart, sa22ImaginaryChart, sb22RealChart, sb22ImaginaryChart,
                s11xRealChart, s11xImaginaryChart,
                s21xRealChart, s21xImaginaryChart,
                s12xRealChart, s12xImaginaryChart,
                s22xRealChart, s22xImaginaryChart));

        chartService.addZoomForCharts(charts);
        updateUI();
    }

    public void applyButtonPressed() {
        if (xAutoCheckBox.isSelected()) {
            xMinTextField.clear();
            xMaxTextField.clear();
            chartService.setXAxisAutoRanging(charts, calculatedModels);
        } else {
            Double lowerBound = Double.parseDouble(xMinTextField.getText());
            Double upperBound = Double.parseDouble(xMaxTextField.getText());
            chartService.setXAxisRanging(charts, lowerBound, upperBound);
        }

        if (yAutoCheckBox.isSelected()) {
            yMinTextField.clear();
            yMaxTextField.clear();
            chartService.setYAxisAutoRanging(charts);
        } else {
            Double lowerBound = Double.parseDouble(yMinTextField.getText());
            Double upperBound = Double.parseDouble(yMaxTextField.getText());
            chartService.setYAxisRanging(charts, lowerBound, upperBound);
        }
    }

    public void frequencyChose() {
        createCharts();
        updateUI();
    }

    public void calculateButtonPressed() {
        createCharts();
    }

    public void measureButtonPressed() {
        if (micranConnectService.isConnected()) {
            String startFrequency = StringUtils.stringOrDefault(startFrequencyTextField.getText(), DEFAULT_START_FREQUENCY);
            String stopFrequency = StringUtils.stringOrDefault(stopFrequencyTextField.getText(), DEFAULT_STOP_FREQUENCY);
            String points = StringUtils.stringOrDefault(pointsTextField.getText(), DEFAULT_POINTS);

            String trc1Measurements = micranConnectService.fetchMeasurementData("Trc1");
            String trc2Measurements = micranConnectService.fetchMeasurementData("Trc2");
            String trc3Measurements = micranConnectService.fetchMeasurementData("Trc3");
            String trc4Measurements = micranConnectService.fetchMeasurementData("Trc4");

            List<MeasurementModel> measurements = micranMeasurementsService.parseMeasurementsFromMicran(trc1Measurements, trc2Measurements, trc3Measurements, trc4Measurements,
                    startFrequency, stopFrequency, points, frequencyComboBox.getValue());

            micranMeasurements.add(measurements);
        }

        updateUI();
    }

    public void resetMeasurementsButtonPressed() {
        micranMeasurements.clear();
        chartService.clearCharts(charts);
        updateUI();
    }

    public void xAutoCheckBoxPressed() {
        if (xAutoCheckBox.isSelected()) {
            xMinTextField.setDisable(true);
            xMaxTextField.setDisable(true);
            return;
        }

        xMinTextField.setDisable(false);
        xMaxTextField.setDisable(false);
    }

    public void yAutoCheckBoxPressed() {
        if (yAutoCheckBox.isSelected()) {
            yMinTextField.setDisable(true);
            yMaxTextField.setDisable(true);
            return;
        }

        yMinTextField.setDisable(false);
        yMaxTextField.setDisable(false);
    }

    public void connectButtonPressed() {
        micranConnectService.connect();
        boolean connected = micranConnectService.isConnected();

        updateUI();

        if (connected) {
            micranConnectService.createTraces();

            String startFrequency = StringUtils.stringOrDefault(startFrequencyTextField.getText(), DEFAULT_START_FREQUENCY);
            String stopFrequency = StringUtils.stringOrDefault(stopFrequencyTextField.getText(), DEFAULT_STOP_FREQUENCY);
            String points = StringUtils.stringOrDefault(pointsTextField.getText(), DEFAULT_POINTS);

            micranConnectService.makeInitialSettings(startFrequency, stopFrequency, points, frequencyComboBox.getValue());

            updateUI();
        }
    }

    public void disconnectButtonPressed() {
        micranConnectService.disconnect();
        updateUI();
    }

    public void save() {
        fileService.save(calculatedModels);
    }

    private void addTextFormatters() {
        ChangeTransformer doubleValidation = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        };
        ChangeTransformer intValidation = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> xMinFormatter = new TextFormatter<>(doubleValidation::apply);
        TextFormatter<String> xMaxFormatter = new TextFormatter<>(doubleValidation::apply);
        TextFormatter<String> yMinFormatter = new TextFormatter<>(doubleValidation::apply);
        TextFormatter<String> yMaxFormatter = new TextFormatter<>(doubleValidation::apply);

        TextFormatter<String> startFrequencyFormatter = new TextFormatter<>(doubleValidation::apply);
        TextFormatter<String> stopFrequencyFormatter = new TextFormatter<>(doubleValidation::apply);
        TextFormatter<String> pointsFormatter = new TextFormatter<>(intValidation::apply);

        xMinTextField.setTextFormatter(xMinFormatter);
        xMaxTextField.setTextFormatter(xMaxFormatter);
        yMinTextField.setTextFormatter(yMinFormatter);
        yMaxTextField.setTextFormatter(yMaxFormatter);

        startFrequencyTextField.setTextFormatter(startFrequencyFormatter);
        stopFrequencyTextField.setTextFormatter(stopFrequencyFormatter);
        pointsTextField.setTextFormatter(pointsFormatter);
    }

    private void initializeFrequencyComboBox() {
        if (frequencyComboBox != null && frequencyComboBox.getItems() != null) {
            frequencyComboBox.getItems().add(FrequencyAbbreviations.HZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.KHZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.MHZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.GHZ.getAbbreviation());
            frequencyComboBox.setValue(FrequencyAbbreviations.GHZ.getAbbreviation());
        }
    }

    private void updateUI() {
        connectionIndicator.setFill(micranConnectService.isConnected() ? Color.LIMEGREEN : Color.RED);
        deviceIdLabel.setText(micranConnectService.getLastId());

        measurementsCounterLabel.setText("Измерений выполнено: " + micranMeasurements.size());
        startFrequencyLabel.setText("Начальная частота (" + frequencyComboBox.getValue() + "):");
        stopFrequencyLabel.setText("Начальная частота (" + frequencyComboBox.getValue() + "):");
    }

    private void createCharts() {
        if (micranMeasurements.size() == 2) {
            calculatedModels = calculationService.calculate(micranMeasurements.getFirst(), micranMeasurements.getLast(), frequencyComboBox.getValue());
            chartService.clearCharts(charts);
            chartService.createCharts(charts, calculatedModels);
        }
    }
}

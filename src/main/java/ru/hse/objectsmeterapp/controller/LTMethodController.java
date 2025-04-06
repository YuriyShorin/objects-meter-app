package ru.hse.objectsmeterapp.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import ru.hse.objectsmeterapp.enums.FrequencyAbbreviations;
import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.model.CalculatedModel;
import ru.hse.objectsmeterapp.model.S2PFileModel;
import ru.hse.objectsmeterapp.service.AlertService;
import ru.hse.objectsmeterapp.service.CalculationService;
import ru.hse.objectsmeterapp.service.ChartService;
import ru.hse.objectsmeterapp.service.FileService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LTMethodController {

    private final FileService fileService;

    private final CalculationService calculationService;

    private final AlertService alertService;

    private final ChartService chartService;

    private final Map<String, S2PFileModel> s2PFileModelsByFileNames;

    private final List<LineChart<Number, Number>> charts;

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
    private ComboBox<String> lFileComboBox;

    @FXML
    private ComboBox<String> tFileComboBox;

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

    public LTMethodController() {
        this.fileService = new FileService();
        this.alertService = new AlertService();
        this.chartService = new ChartService();
        this.calculationService = new CalculationService();
        this.s2PFileModelsByFileNames = new HashMap<>();
        this.charts = new ArrayList<>();
        this.calculatedModels = new ArrayList<>();
    }

    public void initialize() {
        if (frequencyComboBox != null && frequencyComboBox.getItems() != null) {
            frequencyComboBox.getItems().add(FrequencyAbbreviations.HZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.KHZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.MHZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.GHZ.getAbbreviation());
            frequencyComboBox.setValue(FrequencyAbbreviations.GHZ.getAbbreviation());
        }
        charts.addAll(List.of(sa11RealChart, sa11ImaginaryChart, sb11RealChart, sb11ImaginaryChart,
                sa12RealChart, sa12ImaginaryChart, sb12RealChart, sb12ImaginaryChart,
                sa21RealChart, sa21ImaginaryChart, sb21RealChart, sb21ImaginaryChart,
                sa22RealChart, sa22ImaginaryChart, sb22RealChart, sb22ImaginaryChart));
    }

    public void openFiles() {
        List<File> files = fileService.chooseFiles();
        if (files.isEmpty()) {
            return;
        }

        if (files.size() < 2) {
            alertService.showErrorAlert("Выберете хотя бы 2 файла");
        }

        List<String> fileNames = new ArrayList<>();
        s2PFileModelsByFileNames.clear();

        files.forEach(file -> {
            S2PFileModel s2PFileModel = fileService.readS2PFile(file);
            s2PFileModelsByFileNames.put(file.getName(), s2PFileModel);
            fileNames.add(file.getName());
        });

        lFileComboBox.getItems().addAll(fileNames);
        lFileComboBox.setValue(files.getFirst().getName());

        tFileComboBox.getItems().addAll(fileNames);
        tFileComboBox.setValue(files.get(1).getName());
    }

    public void save() {
        fileService.save(calculatedModels);
    }

    public void closeFiles() {
        s2PFileModelsByFileNames.clear();
        calculatedModels.clear();
        chartService.clearCharts(charts);
        lFileComboBox.getItems().clear();
        tFileComboBox.getItems().clear();
    }

    public void applyButtonPressed() {
        if (xAutoCheckBox.isSelected()) {
            xMinTextField.clear();
            xMaxTextField.clear();
            chartService.setXAxisAutoRanging(charts, calculatedModels);
        } else {
            try {
                Double lowerBound = Double.parseDouble(xMinTextField.getText());
                Double upperBound = Double.parseDouble(xMaxTextField.getText());
                chartService.setXAxisRanging(charts, lowerBound, upperBound);
            } catch (NumberFormatException e) {
                alertService.showErrorAlert("Неверный формат числа");
            } catch (BusinessException e) {
                alertService.showErrorAlert(e.getMessage());
            }
        }

        if (yAutoCheckBox.isSelected()) {
            yMinTextField.clear();
            yMaxTextField.clear();
            chartService.setYAxisAutoRanging(charts);
        } else {
            try {
                Double lowerBound = Double.parseDouble(yMinTextField.getText());
                Double upperBound = Double.parseDouble(yMaxTextField.getText());
                chartService.setYAxisRanging(charts, lowerBound, upperBound);
            } catch (NumberFormatException e) {
                alertService.showErrorAlert("Неверный формат числа");
            } catch (BusinessException e) {
                alertService.showErrorAlert(e.getMessage());
            }
        }
    }

    public void calculateButtonPressed() {
        createCharts();
    }

    public void frequencyChose() {
        createCharts();
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

    private void createCharts() {
        calculatedModels = calculationService.calculate(s2PFileModelsByFileNames.get(lFileComboBox.getValue()),
                s2PFileModelsByFileNames.get(tFileComboBox.getValue()),
                frequencyComboBox.getValue());
        chartService.clearCharts(charts);
        chartService.createCharts(charts, calculatedModels);
    }
}

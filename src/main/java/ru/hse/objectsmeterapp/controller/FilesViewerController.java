package ru.hse.objectsmeterapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Setter;
import ru.hse.objectsmeterapp.ObjectsMeterFxApplication;
import ru.hse.objectsmeterapp.enums.FrequencyAbbreviations;
import ru.hse.objectsmeterapp.enums.WindowsInfo;
import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.model.S2PFileModel;
import ru.hse.objectsmeterapp.service.AlertService;
import ru.hse.objectsmeterapp.service.ChartService;
import ru.hse.objectsmeterapp.service.FileService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesViewerController {

    private final FileService fileService;

    private final ChartService chartService;

    private final AlertService alertService;

    private final Map<String, S2PFileModel> s2PFileModelsByFileNames;

    private final List<LineChart<Number, Number>> charts;

    @Setter
    private Stage stage;

    @FXML
    private LineChart<Number, Number> s11RealChart;

    @FXML
    private LineChart<Number, Number> s11ImaginaryChart;

    @FXML
    private LineChart<Number, Number> s11ModuleChart;

    @FXML
    private LineChart<Number, Number> s11PhaseChart;

    @FXML
    private LineChart<Number, Number> s21RealChart;

    @FXML
    private LineChart<Number, Number> s21ImaginaryChart;

    @FXML
    private LineChart<Number, Number> s21ModuleChart;

    @FXML
    private LineChart<Number, Number> s21PhaseChart;

    @FXML
    private LineChart<Number, Number> s12RealChart;

    @FXML
    private LineChart<Number, Number> s12ImaginaryChart;

    @FXML
    private LineChart<Number, Number> s12ModuleChart;

    @FXML
    private LineChart<Number, Number> s12PhaseChart;

    @FXML
    private LineChart<Number, Number> s22RealChart;

    @FXML
    private LineChart<Number, Number> s22ImaginaryChart;

    @FXML
    private LineChart<Number, Number> s22ModuleChart;

    @FXML
    private LineChart<Number, Number> s22PhaseChart;

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

    public FilesViewerController() {
        this.fileService = new FileService();
        this.chartService = new ChartService();
        this.alertService = new AlertService();
        s2PFileModelsByFileNames = new HashMap<>();
        charts = new ArrayList<>();
    }

    public void initialize() {
        if (frequencyComboBox != null && frequencyComboBox.getItems() != null) {
            frequencyComboBox.getItems().add(FrequencyAbbreviations.HZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.KHZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.MHZ.getAbbreviation());
            frequencyComboBox.getItems().add(FrequencyAbbreviations.GHZ.getAbbreviation());
            frequencyComboBox.setValue(FrequencyAbbreviations.GHZ.getAbbreviation());
        }
        charts.addAll(List.of(s11RealChart, s11ImaginaryChart, s11ModuleChart, s11PhaseChart,
                s21RealChart, s21ImaginaryChart, s21ModuleChart, s21PhaseChart,
                s12RealChart, s12ImaginaryChart, s12ModuleChart, s12PhaseChart,
                s22RealChart, s22ImaginaryChart, s22ModuleChart, s22PhaseChart));
    }

    public void openFiles() {
        List<File> files = fileService.chooseFiles();
        if (files.isEmpty()) {
            return;
        }

        s2PFileModelsByFileNames.clear();
        files.forEach(file -> {
            S2PFileModel s2PFileModel = fileService.readS2PFile(file);
            s2PFileModelsByFileNames.put(file.getName(), s2PFileModel);
        });

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

    public void applyButtonPressed() {
        if (xAutoCheckBox.isSelected()) {
            chartService.setXAxisAutoRanging(charts);
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

    public void frequencyChose() {
        createCharts();
    }

    public void closeFiles() {
        s2PFileModelsByFileNames.clear();
        chartService.clearCharts(charts);
    }

    private void createCharts() {
        chartService.createViewerCharts(charts, s2PFileModelsByFileNames, frequencyComboBox.getValue());
    }

    public void chooseInstrument() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObjectsMeterFxApplication.class.getResource(WindowsInfo.MAIN.getName()));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(WindowsInfo.MAIN.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        MainWindowController mainWindowController = fxmlLoader.getController();
        mainWindowController.setStage(stage);
    }
}
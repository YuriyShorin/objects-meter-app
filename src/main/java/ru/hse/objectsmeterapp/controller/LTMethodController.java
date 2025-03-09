package ru.hse.objectsmeterapp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
public class LTMethodController {

    private final FileService fileService;

    private final AlertService alertService;

    private final ChartService chartService;

    private final Map<String, S2PFileModel> s2PFileModelsByFileNames;

    private List<LineChart<Number, Number>> charts;

    private List<ScatterChart<Number, Number>> frequencyCharts;

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
    private LineChart<Number, Number> sa22RealChart;

    @FXML
    private LineChart<Number, Number> sa22ImaginaryChart;

    @FXML
    private LineChart<Number, Number> sb22RealChart;

    @FXML
    private LineChart<Number, Number> sb22ImaginaryChart;

    @FXML
    private ScatterChart<Number, Number> frequencyChartSa11Real;

    @FXML
    private ScatterChart<Number, Number> frequencyChartSa11Imaginary;

    @FXML
    private ScatterChart<Number, Number> frequencyChartSb11Real;

    @FXML
    private ScatterChart<Number, Number> frequencyChartSb11Imaginary;

    @Setter
    private Stage stage;

    public LTMethodController() {
        this.fileService = new FileService();
        this.alertService = new AlertService();
        this.chartService = new ChartService();
        this.s2PFileModelsByFileNames = new HashMap<>();
        this.charts = new ArrayList<>();
        this.frequencyCharts = new ArrayList<>();
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
                sa22RealChart, sa22ImaginaryChart, sb22RealChart, sb22ImaginaryChart));
        frequencyCharts.addAll(List.of(frequencyChartSa11Real, frequencyChartSa11Imaginary, frequencyChartSb11Real, frequencyChartSb11Imaginary));
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

        //  createCharts();
    }

    public void closeFiles() {
        s2PFileModelsByFileNames.clear();
        chartService.clearCharts(charts);
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
        chartService.clearCharts(charts);

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

        createCharts();
    }

    public void frequencyChose() {
        createCharts();
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

    private void createCharts() {
        chartService.clearCharts(charts);
        chartService.createLTCharts(charts, frequencyCharts, s2PFileModelsByFileNames.get(lFileComboBox.getValue()),
                s2PFileModelsByFileNames.get(tFileComboBox.getValue()), frequencyComboBox.getValue());
    }
}

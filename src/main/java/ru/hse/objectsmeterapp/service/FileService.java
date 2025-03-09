package ru.hse.objectsmeterapp.service;

import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import org.apache.commons.numbers.complex.Complex;
import ru.hse.objectsmeterapp.model.MeasurementModel;
import ru.hse.objectsmeterapp.model.S2PFileModel;
import ru.hse.objectsmeterapp.utils.ComplexNumbersUtils;
import ru.hse.objectsmeterapp.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private static final String INITIAL_DIRECTORY_PATH = "C:\\Users";
    private static final String FILE_DESCRIPTION = "Файл \"S2P\"";
    private static final String FILE_EXTENSION_PATTERN = "*.s2p";

    private static final String COMMENT_PREFIX = "!";
    private static final String HEADER_PREFIX = "#";

    public List<File> chooseFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(INITIAL_DIRECTORY_PATH));

        ObservableList<FileChooser.ExtensionFilter> extensionFilters = fileChooser.getExtensionFilters();
        extensionFilters.add(new FileChooser.ExtensionFilter(FILE_DESCRIPTION, FILE_EXTENSION_PATTERN));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
        return selectedFiles != null ? selectedFiles : new ArrayList<>();
    }

    public S2PFileModel readS2PFile(File file) {
        S2PFileModel s2PFileModel = new S2PFileModel();
        List<String> comments = new ArrayList<>();
        List<String> header = new ArrayList<>();
        List<MeasurementModel> measurements = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            bufferedReader.lines()
                    .forEach(line -> {

                        if (line.isEmpty()) {
                            return;
                        }
                        line = StringUtils.clearBlanks(line);

                        if (line.startsWith(COMMENT_PREFIX)) {
                            String lineCleared = StringUtils.clear(line, COMMENT_PREFIX);
                            comments.add(lineCleared);
                            return;
                        }

                        if (line.startsWith(HEADER_PREFIX)) {
                            String lineCleared = StringUtils.clear(line, HEADER_PREFIX);
                            List<String> lineSplit = StringUtils.split(lineCleared);
                            header.addAll(lineSplit);
                            return;
                        }

                        MeasurementModel measurementModel = readMeasurement(line, header.getFirst().toUpperCase(), header.get(2).toUpperCase());
                        measurements.add(measurementModel);
                    });
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        s2PFileModel.setComments(comments);
        s2PFileModel.setHeader(header);
        s2PFileModel.setMeasurements(measurements);

        return s2PFileModel;
    }

    private MeasurementModel readMeasurement(String line, String frequencyAbbreviation, String measurementAbbreviation) {
        List<String> lineSplit = StringUtils.split(line);
        MeasurementModel measurementModel = new MeasurementModel();
        double frequency = Double.parseDouble(lineSplit.getFirst());
        List<Complex> sValues = new ArrayList<>();

        for (int i = 1; i < lineSplit.size(); i = i + 2) {
            Complex sValue;
            double value1 = Double.parseDouble(new BigDecimal(lineSplit.get(i)).toPlainString());
            double value2 = Double.parseDouble(new BigDecimal(lineSplit.get(i + 1)).toPlainString());

            switch (measurementAbbreviation) {
                case "MA" -> sValue = Complex.ofPolar(value1, value2);
                case "DB" -> sValue = ComplexNumbersUtils.fromPolarCoordinates(value1, value2);
                default -> sValue = Complex.ofCartesian(value1, value2);
            }

            sValues.add(sValue);
        }

        switch (frequencyAbbreviation) {
            case "KHZ" -> frequency = frequency * 1000;
            case "MHZ" -> frequency = frequency * 1000000;
            case "GHZ" -> frequency = frequency * 1000000000;
        }

        measurementModel.setFrequency(frequency);
        measurementModel.setS11(sValues.getFirst());
        measurementModel.setS21(sValues.get(1));
        measurementModel.setS12(sValues.get(2));
        measurementModel.setS22(sValues.get(3));

        return measurementModel;
    }
}

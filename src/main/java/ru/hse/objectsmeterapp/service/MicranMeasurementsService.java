package ru.hse.objectsmeterapp.service;

import org.apache.commons.numbers.complex.Complex;
import ru.hse.objectsmeterapp.model.MeasurementModel;
import ru.hse.objectsmeterapp.utils.NumbersUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

public class MicranMeasurementsService {

    private static final double DEFAULT_START_FREQUENCY = 10000000.0;
    private static final double DEFAULT_STOP_FREQUENCY = 20000000000.0;
    private static final int DEFAULT_POINTS = 500;

    public List<MeasurementModel> parseMeasurementsFromMicran(String trc1Measurements, String trc2Measurements, String trc3Measurements, String trc4Measurements,
                                                              String startFrequency, String stopFrequency, String points) {
        List<MeasurementModel> measurements = new ArrayList<>();

        double startFrequencyParsed = startFrequency.isEmpty()
                ? DEFAULT_START_FREQUENCY
                : Double.parseDouble(startFrequency);
        double stopFrequencyParsed = startFrequency.isEmpty()
                ? DEFAULT_STOP_FREQUENCY
                : Double.parseDouble(stopFrequency);
        int pointsParsed = points.isEmpty()
                ? DEFAULT_POINTS
                : Integer.parseInt(points);
        double step = (stopFrequencyParsed - startFrequencyParsed) / pointsParsed;

        List<Double> frequencyList = DoubleStream.iterate(startFrequencyParsed, hasNext -> hasNext <= stopFrequencyParsed, next -> next + step)
                .boxed()
                .toList();

        String[] trc1MeasurementsSplit = trc1Measurements.split(",");
        String[] trc2MeasurementsSplit = trc2Measurements.split(",");
        String[] trc3MeasurementsSplit = trc3Measurements.split(",");
        String[] trc4MeasurementsSplit = trc4Measurements.split(",");

        int measurementIndex = 0;
        for (Double frequency : frequencyList) {
            Complex s11 = NumbersUtils.parseComplex(trc1MeasurementsSplit[measurementIndex], trc1MeasurementsSplit[measurementIndex + 1]);
            Complex s21 = NumbersUtils.parseComplex(trc2MeasurementsSplit[measurementIndex], trc2MeasurementsSplit[measurementIndex + 1]);
            Complex s12 = NumbersUtils.parseComplex(trc3MeasurementsSplit[measurementIndex], trc3MeasurementsSplit[measurementIndex + 1]);
            Complex s22 = NumbersUtils.parseComplex(trc4MeasurementsSplit[measurementIndex], trc4MeasurementsSplit[measurementIndex + 1]);

            measurements.add(MeasurementModel.builder()
                    .frequency(frequency)
                    .s11(s11)
                    .s21(s21)
                    .s12(s12)
                    .s22(s22)
                    .build());

            measurementIndex += 2;
        }

        return measurements;
    }
}

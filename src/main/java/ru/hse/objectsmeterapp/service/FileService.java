package ru.hse.objectsmeterapp.service;

import ru.hse.objectsmeterapp.exception.BusinessException;
import ru.hse.objectsmeterapp.model.CalculatedModel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class FileService {

    private static final String OUTPUT_DIRECTORY = "C:\\Programms\\Files\\Java\\objects-meter-app\\output\\";
    private static final String FILE_EXTENSION = ".s2p";
    private static final String COMMENTS = """
            !Date: %s
            !Measurements: S11, S21, S12, S22
            """;
    private static final String HEADER = "# Hz S RI R 50";

    public void save(List<CalculatedModel> calculatedModels) {
        if (calculatedModels.isEmpty()) {
            return;
        }

        String filePath = OUTPUT_DIRECTORY + UUID.randomUUID() + FILE_EXTENSION;
        LocalDateTime now = LocalDateTime.now();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            writer.write(String.format(COMMENTS, now));
            writer.newLine();

            writer.write(HEADER);
            writer.newLine();

            for (CalculatedModel calculatedModel : calculatedModels) {
                String line = calculatedModel.getFrequency()
                        + " " + calculatedModel.getS11x().getReal() + " " + calculatedModel.getS11x().getImaginary()
                        + " " + calculatedModel.getS21x().getReal() + " " + calculatedModel.getS21x().getImaginary()
                        + " " + calculatedModel.getS12x().getReal() + " " + calculatedModel.getS12x().getImaginary()
                        + " " + calculatedModel.getS22x().getReal() + " " + calculatedModel.getS22x().getImaginary();
                writer.write(line);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            throw new BusinessException("Ошибка при сохранении файла");
        }
    }
}

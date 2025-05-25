package ru.hse.objectsmeterapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.hse.objectsmeterapp.enums.FileNames;
import ru.hse.objectsmeterapp.enums.Titles;

import java.io.IOException;
import java.util.Objects;

public class ObjectsMeterFxApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObjectsMeterFxApplication.class.getResource(FileNames.MAIN_WINDOW.getName()));

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(FileNames.MAIN_WINDOW_STYLE.getName())).toExternalForm());

        stage.setTitle(Titles.MAIN_WINDOW.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
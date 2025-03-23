package ru.hse.objectsmeterapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.hse.objectsmeterapp.enums.WindowsInfo;

import java.io.IOException;

public class ObjectsMeterFxApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObjectsMeterFxApplication.class.getResource(WindowsInfo.LT_METHOD.getName()));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(WindowsInfo.LT_METHOD.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
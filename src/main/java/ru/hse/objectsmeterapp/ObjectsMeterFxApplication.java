package ru.hse.objectsmeterapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ru.hse.objectsmeterapp.controller.MainWindowController;
import ru.hse.objectsmeterapp.enums.WindowsInfo;

import java.io.IOException;

public class ObjectsMeterFxApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObjectsMeterFxApplication.class.getResource(WindowsInfo.MAIN.getName()));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(WindowsInfo.MAIN.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        MainWindowController mainWindowController = fxmlLoader.getController();
        mainWindowController.setStage(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
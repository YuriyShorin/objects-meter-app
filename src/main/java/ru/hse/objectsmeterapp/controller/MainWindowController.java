package ru.hse.objectsmeterapp.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.stage.Stage;
import lombok.Setter;
import ru.hse.objectsmeterapp.ObjectsMeterFxApplication;
import ru.hse.objectsmeterapp.enums.WindowsInfo;

import java.io.IOException;

@Setter
public class MainWindowController {

    private Stage stage;

    public void viewFilesButtonPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObjectsMeterFxApplication.class.getResource(WindowsInfo.VIEWER.getName()));
        Scene scene = new Scene(fxmlLoader.load());

        FilesViewerController filesViewerController = fxmlLoader.getController();
        filesViewerController.setStage(stage);

        stage.setTitle(WindowsInfo.VIEWER.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LTMethodButtonPressed() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ObjectsMeterFxApplication.class.getResource(WindowsInfo.LT_METHOD.getName()));
        Scene scene = new Scene(fxmlLoader.load());

        LTMethodController ltMethodController = fxmlLoader.getController();
        ltMethodController.setStage(stage);

        stage.setTitle(WindowsInfo.LT_METHOD.getTitle());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}

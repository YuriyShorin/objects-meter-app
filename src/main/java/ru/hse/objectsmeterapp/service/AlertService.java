package ru.hse.objectsmeterapp.service;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertService {

    public void showErrorAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText, ButtonType.OK);
        alert.setTitle("Ошибка");
        alert.showAndWait();
    }
}

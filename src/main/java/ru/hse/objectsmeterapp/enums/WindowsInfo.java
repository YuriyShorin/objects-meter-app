package ru.hse.objectsmeterapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WindowsInfo {

    LT_METHOD("lt-method-window.fxml", "LT метод");

    private final String name;

    private final String title;
}

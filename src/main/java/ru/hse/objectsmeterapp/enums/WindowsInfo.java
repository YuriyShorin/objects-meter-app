package ru.hse.objectsmeterapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WindowsInfo {

    MAIN("main-window.fxml", "Выбор инструмента"),
    VIEWER("viewer-window.fxml", "Просмотр файлов"),
    LT_METHOD("lt-method-window.fxml", "LT метод");

    private final String name;

    private final String title;
}

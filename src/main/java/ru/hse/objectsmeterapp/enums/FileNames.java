package ru.hse.objectsmeterapp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileNames {

    // FXML
    MAIN_WINDOW("window/main-window.fxml"),

    // STYLES
    MAIN_WINDOW_STYLE("style/main-window.css");

    private final String name;
}

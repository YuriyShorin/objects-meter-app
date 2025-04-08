package ru.hse.objectsmeterapp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileNames {

    LT_METHOD("lt-method-window.fxml"),
    STYLES("style/style.css");

    private final String name;
}

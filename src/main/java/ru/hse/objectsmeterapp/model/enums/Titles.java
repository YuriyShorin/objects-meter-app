package ru.hse.objectsmeterapp.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Titles {

    MAIN_WINDOW("Измерение объектов");

    private final String title;
}

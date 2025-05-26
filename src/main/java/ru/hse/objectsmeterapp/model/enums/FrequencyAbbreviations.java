package ru.hse.objectsmeterapp.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FrequencyAbbreviations {

    HZ("Hz"),
    KHZ("kHz"),
    MHZ("MHz"),
    GHZ("GHz");

    private final String abbreviation;
}

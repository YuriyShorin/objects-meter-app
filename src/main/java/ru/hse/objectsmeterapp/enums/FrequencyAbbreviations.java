package ru.hse.objectsmeterapp.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FrequencyAbbreviations {

    HZ("Hz"),
    KHZ("kHz"),
    MHZ("MHz"),
    GHZ("GHz");

    private final String abbreviation;
}

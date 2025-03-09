package ru.hse.objectsmeterapp.model;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class S2PFileModel {

    private List<String> comments;

    private List<String> header;

    private List<MeasurementModel> measurements;
}

module ru.hse.objectsmeterapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires org.apache.commons.numbers.complex;
    requires java.desktop;
    requires commons.math3;
    requires com.sun.jna;

    opens ru.hse.objectsmeterapp to javafx.fxml;
    exports ru.hse.objectsmeterapp;
    exports ru.hse.objectsmeterapp.controller;
    opens ru.hse.objectsmeterapp.controller to javafx.fxml;
    exports ru.hse.objectsmeterapp.model;
    opens ru.hse.objectsmeterapp.model to javafx.fxml;
}
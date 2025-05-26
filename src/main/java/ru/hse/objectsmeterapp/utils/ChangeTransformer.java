package ru.hse.objectsmeterapp.utils;

import javafx.scene.control.TextFormatter;

public interface ChangeTransformer {

    TextFormatter.Change apply(TextFormatter.Change change);
}

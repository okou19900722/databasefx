package com.openjfx.database.app.controls;

import com.openjfx.database.DataCharset;

import com.openjfx.database.model.DatabaseCharsetModel;
import javafx.scene.control.ChoiceBox;

public class DataTypeCheckBox extends ChoiceBox<DatabaseCharsetModel> {

    private final DataCharset charset;

    public DataTypeCheckBox(DataCharset charset) {
        this.charset = charset;
        this.getItems().addAll(charset.getDatabaseCharset());
    }
}

package com.openjfx.database.app.controls;

import com.openjfx.database.DataCharset;

import com.openjfx.database.model.DatabaseCharsetModel;
import org.controlsfx.control.PrefixSelectionChoiceBox;

public class DataTypeCheckBox extends PrefixSelectionChoiceBox<DatabaseCharsetModel> {

    private final DataCharset charset;

    public DataTypeCheckBox(DataCharset charset) {
        this.charset = charset;
        this.getItems().addAll(charset.getDatabaseCharset());
        setOnScrollStarted(event -> {
            System.out.println(event.getDeltaX());
        });
    }
}

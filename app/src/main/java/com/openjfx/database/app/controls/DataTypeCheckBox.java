package com.openjfx.database.app.controls;

import com.openjfx.database.DataCharset;

import com.openjfx.database.model.DatabaseCharsetModel;

import java.util.List;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

public class DataTypeCheckBox extends EditChoiceBox<DatabaseCharsetModel> {

    private final DataCharset charset = DATABASE_SOURCE.getCharset();

    public DataTypeCheckBox() {
        this.getItems().addAll(charset.getDatabaseCharset());
    }

    public List<String> getCollations(final String charsetName) {
        return charset.getCharsetCollations(charsetName);
    }
}

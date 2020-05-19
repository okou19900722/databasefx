package com.openjfx.database.app.controls;

import com.openjfx.database.DataCharset;

import com.openjfx.database.model.DatabaseCharsetModel;

import java.util.List;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

public class DataTypeCheckBox extends EditChoiceBox<DatabaseCharsetModel> {

    private final DataCharset charset = DATABASE_SOURCE.getCharset();

    public DataTypeCheckBox() {
        this.getItems().addAll(charset.getDatabaseCharset());
        textProperty().addListener((observable, oldValue, newValue) -> {
            var text = getText();
            for (DatabaseCharsetModel item : getItems()) {
                var charset = item.getCharset();
                System.out.println(text+"-----"+charset);
                if (charset.equals(text)) {
                    getSelectionModel().select(item);
                    break;
                }
            }
        });
    }

    public List<String> getCollations(final String charsetName) {
        return charset.getCharsetCollations(charsetName);
    }
}

package com.openjfx.database.app.component;

import com.openjfx.database.DataCharset;
import com.openjfx.database.app.controls.EditChoiceBox;
import com.openjfx.database.model.DatabaseCharsetModel;
import io.vertx.core.json.JsonObject;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

public class DesignOptionBox extends VBox {

    public enum FieldDataType {
        /**
         * data type is string
         */
        STRING,
        /**
         * data type is number
         */
        NUMBER,
        /**
         * data type is date-time
         */
        DATETIME,
        /**
         * data type is binary
         */
        BINARY
    }

    private JsonObject json;
    /**
     * database source
     */
    private final DataCharset dataCharset = DATABASE_SOURCE.getCharset();

    public DesignOptionBox(final JsonObject cc, final FieldDataType type) {
        this.json = cc;
        if (type == FieldDataType.STRING) {
            buildString();
        }
        if (type == FieldDataType.NUMBER) {
            buildNumber();
        }
        if (type == FieldDataType.BINARY) {
            buildBinary();
        }
        if (type == FieldDataType.DATETIME) {
            buildDateTime();
        }

        getStyleClass().add("design-table-option");
    }

    private void buildString() {

        var grid = new GridPane();
        var defaultLabel = new Label("默认值:");
        var choiceBox = new EditChoiceBox<String>();
        var charsetLabel = new Label("字符集:");
        var charsetBox = new EditChoiceBox<String>();
        var collationLabel = new Label("排序规则:");
        var collationBox = new EditChoiceBox<String>();
        var checkBox = new CheckBox();
        var binaryLabel = new Label("二进制");
        var hBox = new HBox();
        hBox.setSpacing(10);
        hBox.getChildren().addAll(checkBox, binaryLabel);


        grid.addRow(0, defaultLabel, choiceBox);
        grid.addRow(1, charsetLabel, charsetBox);
        grid.addRow(2, collationLabel, collationBox);
        grid.addRow(3, hBox);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.getRowConstraints().add(new RowConstraints());
        grid.getColumnConstraints().add(new ColumnConstraints());

        choiceBox.getItems().addAll("", "EMPTY STRINGING");

        charsetBox.getItems().addAll(dataCharset.getCharset());

        charsetBox.textProperty().addListener((observable, oldValue, newValue) -> {
            var text = charsetBox.getText();
            var items = charsetBox.getItems();
            for (String charset : items) {
                if (charset.equals(text)) {
                    charsetBox.getSelectionModel().select(charset);
                    var collations = dataCharset.getCharsetCollations(text);
                    var ob = FXCollections.observableList(collations);
                    collationBox.setItems(ob);
                    if (ob.size() > 0) {
                        collationBox.getSelectionModel().select(0);
                    }
                    break;
                }
            }
        });

        this.getChildren().add(grid);
    }

    private void buildNumber() {

    }

    private void buildDateTime() {

    }

    private void buildBinary() {

    }
}

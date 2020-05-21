package com.openjfx.database.app.component;

import com.openjfx.database.DataCharset;
import com.openjfx.database.app.controls.EditChoiceBox;
import io.vertx.core.json.JsonObject;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

public class DesignOptionBox extends VBox {

    private final EditChoiceBox<String> defaultBox = new EditChoiceBox<>();
    private final EditChoiceBox<String> charsetBox = new EditChoiceBox<>();
    private final EditChoiceBox<String> collationBox = new EditChoiceBox<>();
    private final CheckBox incrementCheck = new CheckBox();

    /**
     * database source
     */
    private final DataCharset dataCharset = DATABASE_SOURCE.getCharset();

    public DesignOptionBox() {
        Label autoIncrement = new Label("自增");
        GridPane grid = new GridPane();
        grid.addRow(0, autoIncrement, incrementCheck);
        Label defaultLabel = new Label("默认值:");
        grid.addRow(1, defaultLabel, defaultBox);
        Label charsetLabel = new Label("字符集:");
        grid.addRow(2, charsetLabel, charsetBox);
        Label collationLabel = new Label("排序规则:");
        grid.addRow(3, collationLabel, collationBox);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.getRowConstraints().add(new RowConstraints());
        grid.getColumnConstraints().add(new ColumnConstraints());

        defaultBox.getItems().addAll("", "EMPTY STRINGING");

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

        getStyleClass().add("design-table-option");
    }

    public void updateValue(JsonObject json) {
        if (json == null) {
            return;
        }
        var autoIncrement = json.getBoolean("autoIncrement", false);
        var defaultValue = json.getString("defaultValue", "");
        var charset = json.getString("charset", "");
        var collation = json.getString("collation", "");
        this.charsetBox.setText(charset);
        this.collationBox.setText(collation);
        this.incrementCheck.setSelected(autoIncrement);
        this.defaultBox.setText(defaultValue);
    }

    public JsonObject getJsonResult() {
        var json = new JsonObject();

        json.put("autoIncrement", incrementCheck.isSelected());
        json.put("defaultValue", defaultBox.getText());
        json.put("charset", charsetBox.getText());
        json.put("collation", collationBox.getText());

        return json;
    }

}

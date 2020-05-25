package com.openjfx.database.app.component;

import com.openjfx.database.DataCharset;
import com.openjfx.database.app.controls.EditChoiceBox;
import com.openjfx.database.app.model.DesignTableModel;
import io.vertx.core.json.JsonObject;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    private DesignTableModel model;

    public void updateValue(final DesignTableModel model) {
        this.model = model;

        //update value
        this.charsetBox.setText(model.getCharset());
        this.collationBox.setText(model.getCollation());
        this.incrementCheck.setSelected(model.isAutoIncrement());
        this.defaultBox.setText(model.getDefaultValue());

        //update all listener
        defaultBox.textProperty().addListener((observable, oldValue, newValue) -> this.model.setDefaultValue(newValue));
        charsetBox.textProperty().addListener((observable, oldValue, newValue) -> this.model.setCharset(newValue));
        collationBox.textProperty().addListener((observable, oldValue, newValue) -> this.model.setCollation(newValue));
        incrementCheck.selectedProperty().addListener((observable, oldValue, newValue) -> this.model.setAutoIncrement(newValue));
    }
}

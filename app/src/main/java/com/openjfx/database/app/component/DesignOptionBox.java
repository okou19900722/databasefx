package com.openjfx.database.app.component;

import com.openjfx.database.DataCharset;
import com.openjfx.database.app.controls.EditChoiceBox;
import com.openjfx.database.app.model.DesignTableModel;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ResourceBundle;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;

/**
 * Design table option
 *
 * @author yangkui
 * @since 1.0
 */
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
        defaultBox.setHideSelector(true);

        var autoIncrement = new Label(I18N.getString("view.design.table.option.auto"));
        var grid = new GridPane();
        grid.addRow(0, autoIncrement, incrementCheck);
        var defaultLabel = new Label(I18N.getString("view.design.table.option.default"));
        grid.addRow(1, defaultLabel, defaultBox);
        var charsetLabel = new Label(I18N.getString("view.design.table.option.charset"));
        grid.addRow(2, charsetLabel, charsetBox);
        var collationLabel = new Label(I18N.getString("view.design.table.option.collation"));
        grid.addRow(3, collationLabel, collationBox);

        grid.setHgap(10);
        grid.setVgap(10);
        grid.getRowConstraints().add(new RowConstraints());
        grid.getColumnConstraints().add(new ColumnConstraints());


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

package com.openjfx.database.app.skin;

import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ChoiceBoxSkin;


public class EditChoiceBoxSkin<T> extends ChoiceBoxSkin<T> {

    private final TextField textField = new TextField();

    public EditChoiceBoxSkin(ChoiceBox<T> control) {
        super(control);
        var label = (Label) getChildren().get(0);
        label.setGraphic(textField);
        label.textProperty().addListener((observable, oldValue, newValue) -> {
            label.setText("");
        });
        control.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            var item = control.getItems().get(index);
            textField.setText(item.toString());
        });
    }
}

package com.openjfx.database.app.skin;

import com.openjfx.database.app.controls.EditChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ChoiceBoxSkin;
import javafx.scene.layout.StackPane;

/**
 * simple impl edit choice-box skin
 *
 * @param <T> {@link javafx.scene.control.ChoiceBox} data type
 * @author yangkui
 * @since 1.0
 */
public class EditChoiceBoxSkin<T> extends ChoiceBoxSkin<T> {

    private final TextField textField = new TextField();

    public EditChoiceBoxSkin(EditChoiceBox<T> control) {
        super(control);
        var label = (Label) getChildren().get(0);
        var openButton = (StackPane) getChildren().get(1);
        label.setGraphic(textField);
        label.setGraphicTextGap(0);
        //forbid text show in label
        label.textProperty().addListener((observable, oldValue, newValue) -> {
            label.setText("");
        });
        //listener ChoiceBox select change
        control.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            var item = control.getItems().get(index);
            textField.setText(item.toString());
        });
        //openButton click synchronized text to control
        openButton.setOnMouseClicked(e -> control.setText(textField.getText()));
    }
}

package com.openjfx.database.app.controls;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXDialog;
import com.openjfx.database.DataTypeHelper;
import com.openjfx.database.common.Handler;
import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import static com.openjfx.database.app.utils.AssetUtils.*;

/**
 * customer table TextField
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTextField extends HBox {

    private static final Image EXTENSION_ICON = getLocalImage(20, 20, "extension-icon.png");
    private static final Image DATETIME_ICON = getLocalImage(20, 20, "time-icon.png");


    private final TextField textField = new TextField();

    private final StringProperty text = textField.textProperty();

    private enum InputType {
        /**
         * String
         */
        STRING,
        /**
         * number
         */
        NUMBER,
        /**
         * datetime
         */
        DATETIME
    }


    public TableTextField(final String text, final TableColumnMeta meta) {
        final var extension = new JFXButton();
        setAlignment(Pos.CENTER);
        setText(text);
        setSpacing(0);
        HBox.setHgrow(textField, Priority.ALWAYS);
        getChildren().addAll(textField, extension);
        if (DataTypeHelper.dateTime(meta.getType())) {
            extension.setGraphic(new ImageView(DATETIME_ICON));
        } else {
            extension.setGraphic(new ImageView(EXTENSION_ICON));
        }
        extension.setOnAction(event -> {
            var type = meta.getType();
            if (DataTypeHelper.dateTime(type)) {
                return;
            }
            var inputType = InputType.STRING;
            if (DataTypeHelper.number(type)) {
                inputType = InputType.NUMBER;
            }
            var dialog = new InputDialog(text, inputType);
            textField.textProperty().bind(dialog.textProperty());
            dialog.showAndWait();
            textField.textProperty().unbind();
        });
        //add css class
        getStyleClass().add("table-text-field");
        getStylesheets().add("css/table-text-field.css");
    }

    public void setActionEvent(Handler<Void, String> handler) {
        textField.setOnAction(e -> {
            handler.handler(textField.getText());
            e.consume();
        });
    }

    public void selectAll() {
        textField.requestFocus();
        textField.selectAll();
    }

    public void setOnKeyRelease(EventHandler<KeyEvent> event) {
        textField.setOnKeyPressed(event);
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    private static class InputDialog extends Dialog<String> {

        private final TextArea textArea = new TextArea();

        /**
         * @param text      target text
         * @param inputType input type
         */
        public InputDialog(final String text, final InputType inputType) {
            var pane = getDialogPane();
            textArea.setText(text);
            textArea.setWrapText(true);
            textArea.setOnInputMethodTextChanged(e -> {
                var commit = e.getCommitted();
                //only inout number
                if (inputType == InputType.NUMBER) {
                    var pos = e.getCaretPosition();
                    System.out.println(pos);
                }
                System.out.println(commit);
            });
            textArea.setPadding(Insets.EMPTY);
            pane.setContent(textArea);
            pane.setPadding(Insets.EMPTY);
            pane.getStylesheets().add("css/base.css");
        }

        public StringProperty textProperty() {
            return textArea.textProperty();
        }
    }
}

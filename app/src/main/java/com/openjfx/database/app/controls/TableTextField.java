package com.openjfx.database.app.controls;

import com.openjfx.database.common.Handler;
import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import net.sf.jsqlparser.schema.Table;

/**
 * customer table TextField
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTextField extends HBox {
    private final HBox hBox = new HBox();
    private final Label label = new Label("type");
    private final Label extension = new Label("extension");
    private final TextField textField = new TextField();

    private final StringProperty text = textField.textProperty();

    public TableTextField(final String text, final TableColumnMeta meta) {
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(0);
        hBox.getChildren().addAll(label, textField, extension);

    }

    public void setActionEvent(Handler<Void, String> handler) {
        textField.setOnAction(e -> {
            handler.handler(textField.getText());
            e.consume();
        });
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
}

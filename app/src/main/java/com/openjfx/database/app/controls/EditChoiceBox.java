package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.EditChoiceBoxSkin;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Skin;

/**
 * simple impl edit choice-box
 *
 * @param <T> {@inheritDoc}
 * @author yangkui
 * @since 1.0
 */
public class EditChoiceBox<T> extends ChoiceBox<T> {
    /**
     * TextField text property
     */
    private final StringProperty text = new SimpleStringProperty("");

    private final static String DEFAULT_STYLE_CLASS = "edit-choice-box";

    public EditChoiceBox() {
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EditChoiceBoxSkin<>(this);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }
}

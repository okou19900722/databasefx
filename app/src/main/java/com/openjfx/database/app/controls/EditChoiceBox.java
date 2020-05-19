package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.EditChoiceBoxSkin;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Skin;

public class EditChoiceBox<T> extends ChoiceBox<T> {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new EditChoiceBoxSkin<>(this);
    }
}

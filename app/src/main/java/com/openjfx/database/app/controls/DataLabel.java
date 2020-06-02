package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.DataLabelSkin;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Custom data label display component
 *
 * @author yangkui
 * @since 1.0
 */
public class DataLabel extends Control {

    private String text;

    public DataLabel(String text) {
        this.text = text;
    }

    public DataLabel() {
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new DataLabelSkin(this);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

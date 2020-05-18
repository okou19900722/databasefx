package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.TableDataViewSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

public class DesignTableView<T> extends TableView<T> {
    @Override
    protected Skin<?> createDefaultSkin() {
        return new TableDataViewSkin(this);
    }
}

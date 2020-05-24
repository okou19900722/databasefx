package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.TableDataViewSkin;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

public class DesignTableView<T> extends TableView<T> {
    {
        setSortPolicy(e->null);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TableDataViewSkin(this);
    }
}

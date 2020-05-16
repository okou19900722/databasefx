package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.TableDataViewSkin;
import javafx.scene.control.Skin;
import javafx.scene.control.TableView;

public class DesignTableView extends TableView {

    @Override
    protected Skin<?> createDefaultSkin() {
        return new TableDataViewSkin(this);
    }
}

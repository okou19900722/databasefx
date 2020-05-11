package com.openjfx.database.app.controls;

import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

/**
 * customer TableColumn
 *
 * @author yangkui
 * @since 1.0
 */
public class TableDataColumn extends TableColumn<ObservableList<StringProperty>, String> {
    /**
     * database table column meta data
     */
    private final TableColumnMeta meta;

    public TableDataColumn(TableColumnMeta meta) {
        this.meta = meta;
        setText(meta.getField());
    }
}

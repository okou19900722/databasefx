package com.openjfx.database.app.model.impl;

import com.openjfx.database.app.model.AbstractDesignTableChangeModel;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;

import java.util.List;

public class TableIndexChangeModel extends AbstractDesignTableChangeModel {
    @Override
    public void addChange(RowChangeModel.ChangeType type, int rowIndex, String fieldName, String oldValue, String newValue) {

    }

    @Override
    public String getUpdateSql(String tableName, List<TableColumnMeta> tableColumnMetas) {
        return null;
    }
}

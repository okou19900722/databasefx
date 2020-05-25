package com.openjfx.database.app.model;

import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;

import java.util.List;

/**
 * design table data model change record
 *
 * @author yangkui
 * @since 1.0
 */
public abstract class AbstractDesignTableChangeModel {
    /**
     * add change
     *
     * @param type      change type
     * @param rowIndex  row index
     * @param fieldName change column name
     * @param oldValue  change before value
     * @param newValue  change after value
     */
    public abstract void addChange(RowChangeModel.ChangeType type, int rowIndex, String fieldName, String oldValue, String newValue);

    /**
     * obtain update sql statement
     *
     * @param tableName        table name
     * @param tableColumnMetas table column meta
     */
    public abstract String getUpdateSql(String tableName, List<TableColumnMeta> tableColumnMetas);

}

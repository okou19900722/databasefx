package com.openjfx.database.app.model.impl;

import com.openjfx.database.app.model.AbstractDesignTableChangeModel;
import com.openjfx.database.model.ColumnChangeModel;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

/**
 * @author yangkui
 * @since 1.0
 */
public class RegularFieldTableChangeModel extends AbstractDesignTableChangeModel {
    /**
     * cached all field change
     */
    private final List<RowChangeModel> changeModels = new ArrayList<>();

    @Override
    public void addChange(RowChangeModel.ChangeType changeType, int rowIndex, String fieldName, String oldValue, String newValue) {
        final var _rowIndex = getRealRowIndex(rowIndex);
        var optional = changeModels.stream().filter(model -> model.getRowIndex() == _rowIndex).findAny();
        if (optional.isPresent()) {
            var model = optional.get();
            var changes = model.getColumnChangeModels();
            var cOptional = changes.stream().filter(column -> column.getFieldName().equals(fieldName)).findAny();
            if (cOptional.isPresent()) {
                var col = cOptional.get();
                if (changeType == RowChangeModel.ChangeType.UPDATE) {
                    if (col.getOriginValue().equals(newValue)) {
                        changes.remove(col);
                        if (changes.isEmpty()) {
                            changeModels.remove(model);
                        }
                    } else {
                        col.setNewValue(newValue);
                    }
                }
                if (changeType == RowChangeModel.ChangeType.CREATE) {
                    col.setNewValue(newValue);
                }
            } else {
                var column = new ColumnChangeModel();
                column.setOriginValue(oldValue);
                column.setNewValue(newValue);
                column.setFieldName(fieldName);
                changes.add(column);
            }
        } else {
            var rowChange = new RowChangeModel(_rowIndex, changeType);
            var columns = new ArrayList<ColumnChangeModel>();
            rowChange.setColumnChangeModels(columns);

            //Only update actions are detected, delete actions are ignored
            if (changeType == RowChangeModel.ChangeType.UPDATE) {
                var column = new ColumnChangeModel();
                column.setOriginValue(oldValue);
                column.setNewValue(newValue);
                column.setFieldName(fieldName);
                columns.add(column);
            }

            changeModels.add(rowChange);
        }
    }

    @Override
    public String getUpdateSql(String tableName, List<TableColumnMeta> tableColumnMetas) {
        if (changeModels.isEmpty()) {
            return "";
        }
        var generator = DATABASE_SOURCE.getGenerator();
        var createNum = changeModels.stream().filter(model -> model.getChangeType() == RowChangeModel.ChangeType.CREATE).count();
        var sql = "";
        //create table
        if (createNum == changeModels.size()) {

        } else {
            //update table field value
            sql = generator.createFieldModifySqlStatement(tableName, changeModels, tableColumnMetas);

            var updated = changeModels.stream().filter(model -> model.getChangeType() == RowChangeModel.ChangeType.UPDATE).collect(Collectors.toList());

        }
        return sql;
    }

    /**
     * Exclude the deleted fields and get the real index location
     *
     * @param rowIndex table row index
     * @return real row index
     */
    private int getRealRowIndex(final int rowIndex) {
        var list = changeModels.stream()
                .filter(row -> row.getRowIndex() < rowIndex && row.getChangeType() == RowChangeModel.ChangeType.DELETE).collect(Collectors.toList());
        return rowIndex + list.size();
    }

    @Override
    public String toString() {
        return "RegularFieldTableChangeModel{" +
                "changeModels=" + changeModels +
                '}';
    }
}
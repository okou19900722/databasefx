package com.openjfx.database.app.model;


import com.openjfx.database.enums.DesignTableOperationSource;
import com.openjfx.database.enums.DesignTableOperationType;
import com.openjfx.database.model.ColumnChangeModel;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

/**
 * design table change
 *
 * @author yangkui
 * @since 1.0
 */
public class TableFieldChangeModel {
    /**
     * cached all field change
     */
    private final List<RowChangeModel> changeModels = new ArrayList<>();

    /**
     * table field change
     *
     * @param meta            table column meta
     * @param type            operation type
     * @param rowIndex        row index
     * @param tableColumnEnum column property
     * @param newValue        change value
     */
    public void fieldChange(TableColumnMeta meta, DesignTableOperationType type, int rowIndex, TableColumnMeta.TableColumnEnum tableColumnEnum, String newValue) {
        var _rowIndex = getRealRowIndex(rowIndex, DesignTableOperationSource.TABLE_FIELD);
        var optional = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == DesignTableOperationSource.TABLE_FIELD)
                .filter(rowChangeModel -> rowChangeModel.getRowIndex() == _rowIndex)
                .findAny();
        //row already exist
        if (optional.isPresent()) {
            var row = optional.get();
            var a = row.containField(tableColumnEnum);
            if (a) {
                var col = row.getColumn(tableColumnEnum);
                var originValue = col.getOriginValue();
                if (originValue.equals(newValue)) {
                    var columns = row.getColumnChangeModels();
                    columns.remove(col);
                    if (columns.isEmpty()) {
                        changeModels.remove(row);
                    }
                    return;
                }
                col.setNewValue(newValue);
            } else {
                var col = new ColumnChangeModel(tableColumnEnum);
                var oldValue = meta == null ? "" : meta.getFieldValue(tableColumnEnum).toString();
                col.setFieldName(tableColumnEnum);
                col.setOriginValue(oldValue);
                col.setNewValue(newValue);
                row.getColumnChangeModels().add(col);
            }
        } else {
            var columns = new ArrayList<ColumnChangeModel>();
            if (tableColumnEnum != null) {
                var column = new ColumnChangeModel(tableColumnEnum);
                var oldValue = meta == null ? "" : meta.getFieldValue(tableColumnEnum).toString();
                if (meta == null) {
                    oldValue = "";
                } else {
                    var temp = meta.getFieldValue(tableColumnEnum);
                    if (tableColumnEnum == TableColumnMeta.TableColumnEnum.NULL) {
                        var abc = (Boolean) temp;
                        oldValue = Boolean.valueOf(!abc).toString();
                    } else {
                        oldValue = temp.toString();
                    }
                }
                column.setFieldName(tableColumnEnum);
                column.setOriginValue(oldValue);
                column.setNewValue(newValue);
                columns.add(column);
            }
            var row = new RowChangeModel(_rowIndex, type, DesignTableOperationSource.TABLE_FIELD, columns, meta);
            changeModels.add(row);
        }
    }

    /**
     * delete change row
     *
     * @param source   operation source
     * @param meta     table column meta
     * @param rowIndex row index
     */
    public void deleteChange(TableColumnMeta meta, DesignTableOperationSource source, int rowIndex) {
        var _rowIndex = getRealRowIndex(rowIndex, source);
        var optional = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == source)
                .filter(rowChangeModel -> rowChangeModel.getRowIndex() == _rowIndex)
                .findAny();
        if (optional.isEmpty()) {
            var rowChange = new RowChangeModel(_rowIndex, DesignTableOperationType.DELETE, source, List.of(), meta);
            changeModels.add(rowChange);
        } else {
            var row = optional.get();
            //if the row is new create execute delete the row
            if (row.getOperationType() == DesignTableOperationType.CREATE) {
                changeModels.remove(row);
            }
        }
    }

    public void tableCommentChange(final String original, final String comment) {
        var optional = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == DesignTableOperationSource.TABLE_COMMENT)
                .findAny();
        if (optional.isEmpty()) {
            var column = new ColumnChangeModel(TableColumnMeta.TableColumnEnum.COMMENT);
            column.setOriginValue(original);
            column.setNewValue(comment);
            var row = new RowChangeModel(9999,
                    DesignTableOperationType.UPDATE, DesignTableOperationSource.TABLE_COMMENT, List.of(column), null);
            changeModels.add(row);
        } else {
            var row = optional.get();
            var column = row.getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
            if (comment.equals(column.getOriginValue())) {
                changeModels.remove(row);
            } else {
                column.setNewValue(comment);
            }
        }
    }

    public String getUpdateSql(String tableName, List<TableColumnMeta> tableColumnMetas) {
        if (changeModels.isEmpty()) {
            return "";
        }
        var generator = DATABASE_SOURCE.getGenerator();
        return generator.createFieldModifySqlStatement(tableName, changeModels, tableColumnMetas);
    }

    public String getCreateSql(String tableName) {
        if (changeModels.isEmpty()) {
            return "";
        }
        var generator = DATABASE_SOURCE.getGenerator();
        return generator.createTable(tableName, changeModels);
    }

    /**
     * Exclude the deleted fields and get the real index location
     *
     * @param rowIndex table row index
     * @return real row index
     */
    public int getRealRowIndex(final int rowIndex, DesignTableOperationSource source) {
        var list = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == source)
                .filter(row -> row.getRowIndex() < rowIndex && row.getOperationType() == DesignTableOperationType.DELETE).collect(Collectors.toList());
        return rowIndex + list.size();
    }

    public void clear() {
        changeModels.clear();
    }
}

package com.openjfx.database.model;

import java.util.List;

/**
 * row change detail
 *
 * @author yangkui
 * @since 1.0
 */
public class RowChangeModel {
    /**
     * Model change type
     *
     * @author yangkui
     * @since 1.0
     */
    public enum ChangeType {
        /**
         * update
         */
        UPDATE,
        /**
         * delete
         */
        DELETE,
        /**
         * create
         */
        CREATE
    }

    /**
     * operation type
     */
    public enum OperationType {
        /**
         * table field
         */
        TABLE_FIELD,
        /**
         * table comment
         */
        TABLE_COMMENT,
        /**
         * table index
         */
        TABLE_INDEX,
        /**
         * table trigger
         */
        TABLE_TRIGGER
    }

    private final int rowIndex;
    private final ChangeType changeType;
    private final OperationType operationType;
    private List<ColumnChangeModel> columnChangeModels;

    public RowChangeModel(int rowIndex, ChangeType changeType, OperationType operationType) {
        this.rowIndex = rowIndex;
        this.changeType = changeType;
        this.operationType = operationType;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public List<ColumnChangeModel> getColumnChangeModels() {
        return columnChangeModels;
    }

    public void setColumnChangeModels(List<ColumnChangeModel> columnChangeModels) {
        this.columnChangeModels = columnChangeModels;
    }

    public boolean containField(String field) {
        System.out.println(columnChangeModels);
        var optional = columnChangeModels.stream().filter(column -> column.getFieldName()
                .equals(field)).findAny();
        return optional.isPresent();
    }

    public ColumnChangeModel getColumn(String field) {
        var optional = columnChangeModels.stream().filter(column -> column.getFieldName().equals(field)).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new RuntimeException("Column not find");
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public OperationType getOperationType() {
        return operationType;
    }
}
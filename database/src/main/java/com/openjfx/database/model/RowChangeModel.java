package com.openjfx.database.model;

import com.openjfx.database.enums.DesignTableOperationSource;
import com.openjfx.database.enums.DesignTableOperationType;

import java.util.List;
import java.util.Optional;

/**
 * row change detail
 *
 * @author yangkui
 * @since 1.0
 */
public class RowChangeModel {

    private final int rowIndex;
    private final DesignTableOperationType operationType;
    private final DesignTableOperationSource source;
    private List<ColumnChangeModel> columnChangeModels;
    private final TableColumnMeta tableColumnMeta;

    public RowChangeModel(int rowIndex, DesignTableOperationType operationType, DesignTableOperationSource source, List<ColumnChangeModel> columnChangeModels, TableColumnMeta tableColumnMeta) {
        this.rowIndex = rowIndex;
        this.operationType = operationType;
        this.source = source;
        this.columnChangeModels = columnChangeModels;
        this.tableColumnMeta = tableColumnMeta;
    }


    public List<ColumnChangeModel> getColumnChangeModels() {
        return columnChangeModels;
    }

    public void setColumnChangeModels(List<ColumnChangeModel> columnChangeModels) {
        this.columnChangeModels = columnChangeModels;
    }

    public boolean containField(TableColumnMeta.TableColumnEnum tableColumnEnum) {
        var optional = columnChangeModels.stream()
                .filter(column -> column.getFieldName() == tableColumnEnum).findAny();
        return optional.isPresent();
    }

    public ColumnChangeModel getColumn(TableColumnMeta.TableColumnEnum tableColumnEnum) {
        var optional = columnChangeModels.stream()
                .filter(column -> column.getFieldName() == tableColumnEnum).findAny();
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new RuntimeException("Column not find");
    }

    public Optional<ColumnChangeModel> getFixColumn(TableColumnMeta.TableColumnEnum tableColumnEnum) {
        return columnChangeModels.stream()
                .filter(column -> column.getFieldName() == tableColumnEnum).findAny();
    }

    public String getColumnValueOrGet(TableColumnMeta.TableColumnEnum tableColumnEnum, String defaultValue) {
        var optional = columnChangeModels.stream()
                .filter(column -> column.getFieldName() == tableColumnEnum).findAny();
        if (optional.isEmpty()) {
            return defaultValue;
        } else {
            return optional.get().getNewValue();
        }
    }

    public TableColumnMeta getTableColumnMeta() {
        return tableColumnMeta;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public DesignTableOperationType getOperationType() {
        return operationType;
    }

    public DesignTableOperationSource getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "RowChangeModel{" +
                "rowIndex=" + rowIndex +
                ", operationType=" + operationType +
                ", source=" + source +
                ", columnChangeModels=" + columnChangeModels +
                ", tableColumnMeta=" + tableColumnMeta +
                '}';
    }
}
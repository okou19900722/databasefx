package com.openjfx.database.mysql.impl;

import com.openjfx.database.SQLGenerator;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.enums.DesignTableOperationSource;
import com.openjfx.database.enums.DesignTableOperationType;
import com.openjfx.database.model.ColumnChangeModel;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;
import com.openjfx.database.mysql.MysqlHelper;
import com.openjfx.database.mysql.SQLHelper;
import io.vertx.mysqlclient.MySQLPool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openjfx.database.enums.DesignTableOperationSource.TABLE_COMMENT;
import static com.openjfx.database.enums.DesignTableOperationSource.TABLE_FIELD;

/**
 * Mysql Generator impl
 *
 * @author yangkui
 * @since 1.0
 */
public class SQLGeneratorImpl implements SQLGenerator {
    @Override
    public String createScheme(String name, String charset, String collation) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        var sql = "CREATE DATABASE `" + name + "`";
        if (StringUtils.nonEmpty(charset)) {
            sql += " CHARACTER SET '" + charset + "'";
        }
        if (StringUtils.nonEmpty(collation)) {
            sql += " COLLATE '" + collation + "'";
        }
        return sql;
    }

    @Override
    public String createFieldModifySqlStatement(String table, List<RowChangeModel> changeModels, List<TableColumnMeta> metas) {
//        var tableName = SQLHelper.escapeMysqlField(table);
//        var dataType = new MysqlDataType();
//        var sb = new StringBuilder();
//        sb.append("ALTER TABLE");
//        sb.append(tableName);
//        sb.append(" ");
//        var fieldChanges = changeModels.stream()
//                .filter(rowChangeModel -> rowChangeModel.getSource() == DesignTableOperationSource.TABLE_FIELD).collect(Collectors.toList());
//        for (RowChangeModel rowChangeModel : fieldChanges) {
//            var index = rowChangeModel.getRowIndex();
//            var meta = metas.get(index);
//            if (rowChangeModel.getOperationType() == DesignTableOperationType.UPDATE) {
//                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.FIELD)) {
//                    var column = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.FIELD);
//                    var a = SQLHelper.escapeMysqlField(column.getOriginValue());
//                    var b = SQLHelper.escapeMysqlField(column.getNewValue());
//                    sb.append("CHANGE COLUMN ");
//                    sb.append(a);
//                    sb.append(" ");
//                    sb.append(b);
//                } else {
//                    sb.append("MODIFY COLUMN ");
//                    sb.append(SQLHelper.escapeMysqlField(meta.getField()));
//                    sb.append(" ");
//                }
//                var a = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.TYPE);
//                var b = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.LENGTH);
//                if (a || b) {
//                    var length = dataType.getDataTypeLength(meta.getType());
//                    var type = dataType.getDataType(meta.getType());
//                    //type change
//                    if (a) {
//                        var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.TYPE);
//                        type = col.getNewValue();
//                    }
//                    //length change
//                    if (b) {
//                        var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.LENGTH);
//                        length = col.getNewValue();
//                    }
//                    type = type + "(" + length + ")";
//                    sb.append(type);
//                    sb.append(" ");
//                } else {
//                    sb.append(meta.getType());
//                    sb.append(" ");
//                }
//                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.NULL)) {
//                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.NULL);
//                    var nullable = Boolean.parseBoolean(col.getNewValue());
//                    var ss = nullable ? "NOT NULL " : "NULL ";
//                    sb.append(ss);
//                }
//                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.COMMENT)) {
//                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
//                    sb.append("Comment '");
//                    sb.append(col.getNewValue());
//                    sb.append("' ");
//                }
//                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.CHARSET)) {
//                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.CHARSET);
//                    sb.append("CHARACTER SET ");
//                    sb.append(col.getNewValue());
//                    sb.append(" ");
//                }
//                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.COLLATION)) {
//                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.COLLATION);
//                    sb.append("COLLATE ");
//                    sb.append(col.getNewValue());
//                    sb.append(" ");
//                }
//                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.DEFAULT)) {
//                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.DEFAULT);
//                    sb.append("DEFAULT '");
//                    sb.append(col.getNewValue());
//                    sb.append("' ");
//                }
//            }
////            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.CREATE) {
////
////            }
//            if (rowChangeModel.getOperationType() == DesignTableOperationType.DELETE) {
//                sb.append("DROP COLUMN `");
//                sb.append(meta.getField());
//                sb.append("` ");
//            }
//            sb.append(",");
//        }
//        //Handle primary key changes
//        var keyRows = changeModels.stream().filter(rowChangeModel -> {
//            var columns = rowChangeModel.getColumnChangeModels();
//            for (ColumnChangeModel column : columns) {
//                if (TableColumnMeta.TableColumnEnum.KEY == column.getFieldName()) {
//                    return true;
//                }
//            }
//            return false;
//        }).collect(Collectors.toList());
//        //obtain table original key
//        var keys = metas.stream().filter(meta -> StringUtils.nonEmpty(meta.getKey())).collect(Collectors.toList());
//        //key already happen change
//        if (keyRows.size() > 0) {
//            sb.append(" DROP PRIMARY KEY,");
//            var j = -1;
//            var a = false;
//            for (RowChangeModel keyRow : keyRows) {
//                var index = keyRow.getRowIndex();
//                var meta = metas.get(index);
//                var keyColumn = keyRow.getColumn(TableColumnMeta.TableColumnEnum.KEY);
//
//                var keyStatus = Boolean.parseBoolean(keyColumn.getNewValue());
//
//                j++;
//
//                if (!keyStatus) {
//                    keys.remove(meta);
//                    continue;
//                }
//                if (!a) {
//                    sb.append(" ADD PRIMARY KEY (");
//                    a = true;
//                }
//                if (keyRow.containField(TableColumnMeta.TableColumnEnum.FIELD)) {
//                    var column = keyRow.getColumn(TableColumnMeta.TableColumnEnum.FIELD);
//                    sb.append(SQLHelper.escapeMysqlField(column.getNewValue()));
//                } else {
//                    sb.append(SQLHelper.escapeMysqlField(meta.getField()));
//                }
//                keys.remove(meta);
//                if (j < keyRows.size() - 1) {
//                    sb.append(",");
//                }
//            }
//            var k = 0;
//            for (TableColumnMeta key : keys) {
//                if (k == 0) {
//                    sb.append(",");
//                }
//                sb.append(SQLHelper.escapeMysqlField(key.getField()));
//                if (k < keys.size() - 1) {
//                    sb.append(",");
//                }
//                k++;
//            }
//            if (a) {
//                sb.append(") USING BTREE,");
//            }
//        }
//        //table comment
//        var optional = changeModels.stream().filter(
//                rowChangeModel -> rowChangeModel.getSource() == TABLE_COMMENT).findAny();
//        if (optional.isPresent()) {
//            var rowChange = optional.get();
//            var column = rowChange.getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
//            sb.append(" COMMENT='");
//            sb.append(column.getNewValue());
//            sb.append("';");
//        }
//        var sql = sb.toString();
//        sql = sql.substring(0, sql.length() - 1) + ";";
//        return sql;
        return "";
    }

    @Override
    public String createTable(String table, List<RowChangeModel> changeModels) {
        var sb = new StringBuilder();
        var tableName = SQLHelper.escapeMysqlField(table);
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append("(");
        //filter table field and field info not empty
        var rowChangeModels = changeModels
                .stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == TABLE_FIELD)
                .filter(rowChangeModel -> !rowChangeModel.getColumnChangeModels().isEmpty())
                .collect(Collectors.toList());

        var i = 0;
        var keys = new ArrayList<String>();
        for (RowChangeModel changeModel : rowChangeModels) {
            if (changeModel.containField(TableColumnMeta.TableColumnEnum.FIELD)) {
                var field = changeModel.getColumn(TableColumnMeta.TableColumnEnum.FIELD);
                var fieldName = SQLHelper.escapeMysqlField(field.getNewValue());
                sb.append(fieldName).append(" ");
                if (changeModel.containField(TableColumnMeta.TableColumnEnum.KEY)) {
                    keys.add(fieldName);
                }
            } else {
                continue;
            }
            if (changeModel.containField(TableColumnMeta.TableColumnEnum.TYPE)) {
                var type = changeModel.getColumn(TableColumnMeta.TableColumnEnum.TYPE);
                var val = type.getNewValue();
                var length = "(";
                if (changeModel.containField(TableColumnMeta.TableColumnEnum.LENGTH)) {
                    var l = changeModel.getColumn(TableColumnMeta.TableColumnEnum.LENGTH);
                    length += l.getNewValue();
                } else {
                    length += "0";
                }
                if (changeModel.containField(TableColumnMeta.TableColumnEnum.DECIMAL_POINT)) {
                    var point = changeModel.getColumn(TableColumnMeta.TableColumnEnum.DECIMAL_POINT);
                    length += "," + point.getNewValue();
                }
                length += ")";
                val += length;
                sb.append(val);
                sb.append(" ");
            }
            if (changeModel.containField(TableColumnMeta.TableColumnEnum.CHARSET)) {
                var charset = changeModel.getColumn(TableColumnMeta.TableColumnEnum.CHARSET);
                var collate = changeModel.getColumn(TableColumnMeta.TableColumnEnum.COLLATION);
                sb.append("CHARACTER SET ");
                sb.append(charset.getNewValue());
                sb.append(" COLLATE ");
                sb.append(collate.getNewValue());
                sb.append(" ");
            }
            if (changeModel.containField(TableColumnMeta.TableColumnEnum.NULL)) {
                var nullable = changeModel.getColumn(TableColumnMeta.TableColumnEnum.NULL);
                var b = Boolean.parseBoolean(nullable.getNewValue());
                if (b) {
                    sb.append("NOT NULL ");
                } else {
                    sb.append("NULL ");
                }
            }

            if (changeModel.containField(TableColumnMeta.TableColumnEnum.AUTO_INCREMENT)) {
                var autoIncrement = changeModel.getColumn(TableColumnMeta.TableColumnEnum.AUTO_INCREMENT);
                var a = Boolean.parseBoolean(autoIncrement.getNewValue());
                if (a) {
                    sb.append(" AUTO_INCREMENT ");
                }
            }

            if (changeModel.containField(TableColumnMeta.TableColumnEnum.DEFAULT)) {
                var defaultValue = changeModel.getColumn(TableColumnMeta.TableColumnEnum.DEFAULT);
                sb.append("DEFAULT '");
                sb.append(defaultValue.getNewValue());
                sb.append("' ");
            }

            if (changeModel.containField(TableColumnMeta.TableColumnEnum.COMMENT)) {
                var comment = changeModel.getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
                sb.append("COMMENT '");
                sb.append(comment.getNewValue());
                sb.append("' ");
            }

            if (i < changeModels.size() - 1) {
                sb.append(",");
            }
            i++;
        }
        for (int j = 0; j < keys.size(); j++) {
            var key = keys.get(j);
            sb.append(",PRIMARY KEY (");
            sb.append(key);
            if (j == keys.size() - 1) {
                sb.append(") ");
            } else {
                sb.append(",");
            }
        }
        sb.append(") ");
        var optional = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == TABLE_COMMENT)
                .findAny();
        if (optional.isPresent()) {
            var column = optional.get().getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
            sb.append("COMMENT '");
            sb.append(column.getNewValue());
            sb.append("' ");
        }
        sb.append(";");
        return sb.toString();
    }
}

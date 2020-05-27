package com.openjfx.database.mysql.impl;

import com.openjfx.database.SQLGenerator;
import com.openjfx.database.common.utils.StringUtils;
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
        var tableName = SQLHelper.escapeMysqlField(table);
        var dataType = new MysqlDataType();
        var sb = new StringBuilder();
        sb.append("ALTER TABLE");
        sb.append(tableName);
        sb.append(" ");
        var fieldChanges = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getOperationType() == RowChangeModel.OperationType.TABLE_FIELD).collect(Collectors.toList());
        for (RowChangeModel rowChangeModel : fieldChanges) {
            var index = rowChangeModel.getRowIndex();
            var meta = metas.get(index);
            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.UPDATE) {
                if (rowChangeModel.containField("Field")) {
                    var column = rowChangeModel.getColumn("Field");
                    var a = SQLHelper.escapeMysqlField(column.getOriginValue());
                    var b = SQLHelper.escapeMysqlField(column.getNewValue());
                    sb.append("CHANGE COLUMN ");
                    sb.append(a);
                    sb.append(" ");
                    sb.append(b);
                } else {
                    sb.append("MODIFY COLUMN ");
                    sb.append(SQLHelper.escapeMysqlField(meta.getField()));
                    sb.append(" ");
                }
                var a = rowChangeModel.containField("Type");
                var b = rowChangeModel.containField("Length");
                if (a || b) {
                    var length = dataType.getDataTypeLength(meta.getType());
                    var type = dataType.getDataType(meta.getType());
                    //type change
                    if (a) {
                        var col = rowChangeModel.getColumn("Type");
                        type = col.getNewValue();
                    }
                    //length change
                    if (b) {
                        var col = rowChangeModel.getColumn("Length");
                        length = col.getNewValue();
                    }
                    type = type + "(" + length + ")";
                    sb.append(type);
                    sb.append(" ");
                } else {
                    sb.append(meta.getType());
                    sb.append(" ");
                }
                if (rowChangeModel.containField("Nullable")) {
                    var col = rowChangeModel.getColumn("Nullable");
                    var nullable = Boolean.parseBoolean(col.getNewValue());
                    var ss = nullable ? "NOT NULL " : "NULL ";
                    sb.append(ss);
                }
                if (rowChangeModel.containField("Comment")) {
                    var col = rowChangeModel.getColumn("Comment");
                    sb.append("Comment '");
                    sb.append(col.getNewValue());
                    sb.append("' ");
                }
                if (rowChangeModel.containField("Charset")) {
                    var col = rowChangeModel.getColumn("Charset");
                    sb.append("CHARACTER SET ");
                    sb.append(col.getNewValue());
                    sb.append(" ");
                }
                if (rowChangeModel.containField("Collation")) {
                    var col = rowChangeModel.getColumn("Collation");
                    sb.append("COLLATE ");
                    sb.append(col.getNewValue());
                    sb.append(" ");
                }
                if (rowChangeModel.containField("Default")) {
                    var col = rowChangeModel.getColumn("Default");
                    sb.append("DEFAULT '");
                    sb.append(col.getNewValue());
                    sb.append("' ");
                }
            }
            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.CREATE) {

            }
            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.DELETE) {
                sb.append("DROP COLUMN `");
                sb.append(meta.getField());
                sb.append("` ");
            }
            sb.append(",");
        }
        //Handle primary key changes
        var keyRows = changeModels.stream().filter(rowChangeModel -> {
            var columns = rowChangeModel.getColumnChangeModels();
            for (ColumnChangeModel column : columns) {
                if ("Key".equals(column.getFieldName())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        //obtain table original key
        var keys = metas.stream().filter(meta -> StringUtils.nonEmpty(meta.getKey())).collect(Collectors.toList());
        //key already happen change
        if (keyRows.size() > 0) {
            sb.append(" DROP PRIMARY KEY,");
            var j = -1;
            var a = false;
            for (RowChangeModel keyRow : keyRows) {
                var index = keyRow.getRowIndex();
                var meta = metas.get(index);
                var keyColumn = keyRow.getColumn("Key");

                var keyStatus = Boolean.parseBoolean(keyColumn.getNewValue());

                j++;

                if (!keyStatus) {
                    keys.remove(meta);
                    continue;
                }
                if (!a) {
                    sb.append(" ADD PRIMARY KEY (");
                    a = true;
                }
                if (keyRow.containField("Field")) {
                    var column = keyRow.getColumn("Field");
                    sb.append(SQLHelper.escapeMysqlField(column.getFieldName()));
                } else {
                    sb.append(SQLHelper.escapeMysqlField(meta.getField()));
                }
                keys.remove(meta);
                if (j < keyRows.size() - 1) {
                    sb.append(",");
                }
            }
            var k = 0;
            for (TableColumnMeta key : keys) {
                if (k == 0) {
                    sb.append(",");
                }
                sb.append(SQLHelper.escapeMysqlField(key.getField()));
                if (k < keys.size() - 1) {
                    sb.append(",");
                }
                k++;
            }
            if (a) {
                sb.append(") USING BTREE,");
            }
        }
        //table comment
        var optional = changeModels.stream().filter(
                rowChangeModel -> rowChangeModel.getOperationType() == RowChangeModel.OperationType.TABLE_COMMENT).findAny();
        if (optional.isPresent()) {
            var rowChange = optional.get();
            var column = rowChange.getColumn("Comment");
            sb.append(" COMMENT='");
            sb.append(column.getNewValue());
            sb.append("';");
        }
        var sql = sb.toString();
        sql = sql.substring(0, sql.length() - 1) + ";";
        return sql;
    }

    @Override
    public String createTable(String table, List<RowChangeModel> changeModels) {
        var sb = new StringBuilder();
        var tableName = SQLHelper.escapeMysqlField(table);
        sb.append("CREATE TABLE ");
        sb.append(tableName);
        sb.append("(");
        var rowChangeModels = changeModels
                .stream()
                .filter(rowChangeModel -> rowChangeModel.getOperationType() == RowChangeModel.OperationType.TABLE_FIELD)
                .collect(Collectors.toList());
        var i = 0;
        var keys = new ArrayList<String>();
        for (RowChangeModel changeModel : rowChangeModels) {
            if (changeModel.containField("Field")) {
                var field = changeModel.getColumn("Field");
                var fieldName = SQLHelper.escapeMysqlField(field.getNewValue());
                sb.append(fieldName).append(" ");
                if (changeModel.containField("Key")) {
                    keys.add(fieldName);
                }
                ;
            } else {
                sb.append(" ");
            }
            if (changeModel.containField("Type")) {
                var type = changeModel.getColumn("Type");
                var val = type.getNewValue();
                var length = "(";
                if (changeModel.containField("Length")) {
                    var l = changeModel.getColumn("Length");
                    length += l.getNewValue();
                } else {
                    length += "0";
                }
                if (changeModel.containField("DecimalPoint")) {
                    var point = changeModel.getColumn("DecimalPoint");
                    length += "," + point.getNewValue();
                }
                length += ")";
                val += length;
                sb.append(val);
                sb.append(" ");
            }
            if (changeModel.containField("Charset")) {
                var charset = changeModel.getColumn("Charset");
                var collate = changeModel.getColumn("Collation");
                sb.append("CHARACTER SET ");
                sb.append(charset.getNewValue());
                sb.append(" COLLATE ");
                sb.append(collate.getNewValue());
                sb.append(" ");
            }

            if (changeModel.containField("AutoIncrement")) {
                var autoIncrement = changeModel.getColumn("AutoIncrement");
                var a = Boolean.parseBoolean(autoIncrement.getNewValue());
                if (a) {
                    sb.append(" AUTO_INCREMENT ");
                }
            }

            if (changeModel.containField("Nullable")) {
                var nullable = changeModel.getColumn("Nullable");
                var b = Boolean.parseBoolean(nullable.getNewValue());
                if (b) {
                    sb.append("NOT NULL ");
                } else {
                    sb.append("NULL ");
                }
            }

            if (changeModel.containField("Default")) {
                var defaultValue = changeModel.getColumn("Default");
                sb.append("DEFAULT '");
                sb.append(defaultValue.getNewValue());
                sb.append("' ");
            }

            if (changeModel.containField("Comment")) {
                var comment = changeModel.getColumn("Comment");
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
            if (j == 0) {
                sb.append(" PRIMARY KEY (");
            }
            sb.append(key);
            if (j == keys.size() - 1) {
                sb.append(") ");
            } else {
                sb.append(",");
            }
        }
        sb.append(") ");
        var optional = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getOperationType() == RowChangeModel.OperationType.TABLE_COMMENT)
                .findAny();
        if (optional.isPresent()) {
            var column = optional.get().getColumn("Comment");
            sb.append("COMMENT '");
            sb.append(column.getNewValue());
            sb.append("' ");
        }
        sb.append(";");
        return sb.toString();
    }
}

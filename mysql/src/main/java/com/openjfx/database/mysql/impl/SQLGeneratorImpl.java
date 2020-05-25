package com.openjfx.database.mysql.impl;

import com.openjfx.database.SQLGenerator;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.ColumnChangeModel;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;
import com.openjfx.database.mysql.MysqlHelper;
import com.openjfx.database.mysql.SQLHelper;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;

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
        var flag = 0;
        for (RowChangeModel rowChangeModel : changeModels) {
            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.UPDATE) {
                var index = rowChangeModel.getRowIndex();
                var meta = metas.get(index);
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
                }
                var a = rowChangeModel.containField("Type");
                var b = rowChangeModel.containField("Length");
                if (a || b) {
                    var length = dataType.getDataTypeLength(meta.getType());
                    var type = dataType.getDataType(meta.getType());
                    if (a) {
                        var col = rowChangeModel.getColumn("Type");
                        type = col.getNewValue();
                    }
                    if (b) {
                        var col = rowChangeModel.getColumn("Length");
                        length = col.getNewValue();
                    }
                    type = type + "(" + length + ")";
                    sb.append(type);
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
                }
                if (rowChangeModel.containField("Collation")) {
                    var col = rowChangeModel.getColumn("Collation");
                    sb.append("COLLATE ");
                    sb.append(col.getNewValue());
                    sb.append(" ");
                }
                if (rowChangeModel.containField("Virtual")) {
                    var col = rowChangeModel.getColumn("Virtual");
                    var value = Boolean.parseBoolean(col.getNewValue());
                    if (value) {
                        sb.append("AS () ");
                    } else {
                        //cancel virtual
                    }
                }
                if (rowChangeModel.containField("Default")) {
                    var col = rowChangeModel.getColumn("Default");
                    sb.append("DEFAULT '");
                    sb.append(col.getNewValue());
                    sb.append("' ");
                }
                if (flag == changeModels.size() - 1) {
                    sb.append(";");
                } else {
                    sb.append(",\r\n");
                }
            }
            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.CREATE) {

            }
            if (rowChangeModel.getChangeType() == RowChangeModel.ChangeType.DELETE) {

            }
            flag++;
        }
        return sb.toString();
    }
}

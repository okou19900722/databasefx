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

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        var tableName = SQLHelper.escapeMysqlField(table);
        var sb = new StringBuilder();
        sb.append("ALTER TABLE ");
        sb.append(tableName);
        var updateFieldList = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == TABLE_FIELD)
                .collect(Collectors.toList());
        var str = updateTableField(updateFieldList, metas);
        sb.append(str);
        var optional = changeModels.stream()
                .filter(rowChangeModel -> rowChangeModel.getSource() == TABLE_COMMENT).findAny();
        optional.ifPresent(rowChangeModel -> {
            var column = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
            sb.append(" ");
            sb.append("COMMENT '");
            sb.append(column.getNewValue());
            sb.append("',");
        });
        var sql = sb.toString();
        return sql.substring(0, sql.length() - 1) + ";";
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
                if (changeModel.containField(TableColumnMeta.TableColumnEnum.PRIMARY_KEY)) {
                    keys.add(fieldName);
                }
            } else {
                i++;
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

    private String updateTableField(List<RowChangeModel> rowChangeModels, List<TableColumnMeta> metas) {
        var sb = new StringBuilder();
        var dataType = new MysqlDataType();
        var primaryKey = new ArrayList<String>();
        var primaryStatus = false;
        var j = 0;
        for (RowChangeModel rowChangeModel : rowChangeModels) {
            var meta = rowChangeModel.getTableColumnMeta();
            var operationType = rowChangeModel.getOperationType();
            var strLength = sb.length();
            if (operationType != DesignTableOperationType.DELETE) {
                if (rowChangeModel.getOperationType() == DesignTableOperationType.UPDATE) {
                    if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.FIELD)) {
                        var column = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.FIELD);
                        sb.append("CHANGE COLUMN ");
                        sb.append(SQLHelper.escapeMysqlField(column.getOriginValue()));
                        sb.append(" ");
                        sb.append(SQLHelper.escapeMysqlField(column.getNewValue()));

                    } else {
                        sb.append("MODIFY COLUMN ");
                        sb.append(SQLHelper.escapeMysqlField(meta.getField()));
                    }
                    sb.append(" ");
                }
                if (operationType == DesignTableOperationType.CREATE) {
                    sb.append("ADD COLUMN ");
                    if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.FIELD)) {
                        var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.FIELD);
                        sb.append(SQLHelper.escapeMysqlField(col.getNewValue()));
                        sb.append(" ");
                    } else {
                        sb.append(" unName ");
                    }
                }
                var a = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.TYPE);
                var b = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.LENGTH);
                var c = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.DECIMAL_POINT);
                if (a || b || c) {
                    final String type;
                    if (a) {
                        var column = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.TYPE);
                        type = column.getNewValue();
                    } else {
                        type = meta.getType();
                    }
                    sb.append(type).append("(");
                    final String length;
                    if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.LENGTH)) {
                        var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.LENGTH);
                        length = col.getNewValue();
                    } else {
                        length = meta.getLength();
                    }
                    sb.append(length);
                    var d = dataType.hasDecimalPoint(type);
                    if (d) {
                        sb.append(",");
                        if (c) {
                            var cc = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.DECIMAL_POINT);
                            sb.append(cc.getNewValue());
                        } else {
                            sb.append(meta.getDecimalPoint());
                        }
                    }
                    sb.append(") ");
                } else {
                    if (meta != null) {
                        sb.append(meta.getOriginalType());
                    }
                }
                var d = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.CHARSET);
                var e = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.COLLATION);
                if (d || e) {
                    sb.append(" CHARACTER SET ");
                    if (d) {
                        var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.CHARSET);
                        sb.append(col.getNewValue());
                    } else {
                        if (meta != null) {
                            sb.append(meta.getCharset());
                        }
                    }
                    sb.append(" COLLATE ");
                    if (e) {
                        var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.COLLATION);

                        sb.append(col.getNewValue());
                    } else {
                        if (meta != null) {
                            sb.append(meta.getCollation());
                        }
                    }
                    sb.append(" ");
                }
                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.NULL)) {
                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.NULL);
                    if (Boolean.parseBoolean(col.getNewValue())) {
                        sb.append(" NOT NULL ");
                    } else {
                        sb.append(" NULL ");
                    }
                } else {
                    if (meta != null) {
                        var n = meta.getNull() ? " NULL " : " NOT NULL";
                        sb.append(n);
                    } else {
                        sb.append(" NULL ");
                    }
                }
                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.AUTO_INCREMENT)) {
                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.AUTO_INCREMENT);
                    if (Boolean.parseBoolean(col.getNewValue())) {
                        sb.append(" AUTO_INCREMENT ");
                    }
                } else {
                    if (meta != null) {
                        if (meta.getPrimaryKey()) {
                            sb.append(" AUTO_INCREMENT ");
                        }
                    }
                }
                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.DEFAULT)) {
                    var defaultValue = rowChangeModel.getColumnValueOrGet(TableColumnMeta.TableColumnEnum.DEFAULT, "");
                    sb.append(" DEFAULT '");
                    sb.append(defaultValue);
                    sb.append("'");
                }
                if (rowChangeModel.containField(TableColumnMeta.TableColumnEnum.COMMENT)) {
                    var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.COMMENT);
                    var comment = col.getNewValue();
                    sb.append(" COMMENT '");
                    sb.append(comment);
                    sb.append("' ");
                }
            } else {
                sb.append("DROP COLUMN ");
                sb.append(SQLHelper.escapeMysqlField(meta.getField()));
            }
            var d = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.PRIMARY_KEY);

            if (d) {
                var col = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.PRIMARY_KEY);
                boolean b = Boolean.parseBoolean(col.getNewValue());
                if (b) {
                    var key = getPrimaryKey(rowChangeModel);
                    primaryKey.add(key);
                } else {
                    //cancel key set
                    primaryStatus = true;
                }
            } else {
                var key = getPrimaryKey(rowChangeModel);
                primaryKey.add(key);
                j++;
            }
            if (strLength != sb.length()) {
                sb.append(",");
            }
        }
        if (primaryStatus) {
            sb.append("DROP PRIMARY KEY,");
        }
        var primarySize = primaryKey.size();
        if (j != primarySize) {
            for (int i = 0; i < primarySize; i++) {
                var key = primaryKey.get(i);
                if (i == 0) {
                    sb.append("ADD PRIMARY KEY(");
                }
                sb.append(key);
                if (i < primaryKey.size() - 1) {
                    sb.append(",");
                } else {
                    sb.append("),");
                }
            }
        }
        return sb.toString();
    }

    private boolean canUpdate(RowChangeModel rowChangeModel) {
        var a = rowChangeModel.getOperationType() == DesignTableOperationType.UPDATE;
        var b = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.PRIMARY_KEY);
        var c = rowChangeModel.getColumnChangeModels().size() == 1;
        return a && !(b && c);
    }

    private String getPrimaryKey(RowChangeModel rowChangeModel) {
        final String key;
        var a = rowChangeModel.containField(TableColumnMeta.TableColumnEnum.FIELD);
        if (a) {
            var column = rowChangeModel.getColumn(TableColumnMeta.TableColumnEnum.FIELD);
            key = column.getNewValue();
        } else {
            var meta = rowChangeModel.getTableColumnMeta();
            Objects.requireNonNull(meta);
            key = meta.getField();
        }
        return key;
    }
}

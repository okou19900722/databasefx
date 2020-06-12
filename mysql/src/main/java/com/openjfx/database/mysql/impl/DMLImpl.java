package com.openjfx.database.mysql.impl;

import com.openjfx.database.DML;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import com.openjfx.database.mysql.SQLHelper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Tuple;

import java.util.*;

import static com.openjfx.database.common.config.StringConstants.*;

/**
 * Mysql DML impl
 *
 * @author yangkui
 * @since 1.0
 */
public class DMLImpl implements DML {

    private final MySQLPool client;

    public DMLImpl(MySQLPool client) {
        this.client = client;
    }

    @Override
    public Future<Integer> batchUpdate(List<Map<String, Object[]>> items, String tableName, List<TableColumnMeta> metas) {
        var optional = metas.stream().filter(col -> StringUtils.nonEmpty(col.getKey())).findFirst();
        var promise = Promise.<Integer>promise();
        if (optional.isEmpty()) {
            promise.fail("无法找到key值,故取消更新");
        } else {
            var keyMeta = optional.get();
            var sql = updateSql(metas, keyMeta, tableName);
            var tuples = new ArrayList<Tuple>();
            for (var item : items) {
                var t = item.get(ROW);
                var tt = toConvertData(t);
                var tuple = Tuple.tuple();
                for (var o : tt) {
                    tuple.addValue(o);
                }
                tuple.addValue(item.get(KEY)[0]);
                tuples.add(tuple);
            }
            client.preparedBatch(sql, tuples).onSuccess(r -> promise.complete(1)).onFailure(promise::fail);
        }

        return promise.future();
    }

    @Override
    public Future<Long> insert(List<TableColumnMeta> metas, Object[] columns, String tableName) {

        var sql = insertSql(metas, tableName);

        var promise = Promise.<Long>promise();

        var tuple = Tuple.wrap(Arrays.asList(columns));

        var future = client.preparedQuery(sql, tuple);
        future.onSuccess(rows -> {
            var lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
            promise.complete(lastInsertId);
        });
        future.onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Object> batchInsert(List<TableColumnMeta> metas, List<Object[]> rows, String tableName) {
        var sql = insertSql(metas, tableName);
        var tuples = new ArrayList<Tuple>();

        for (var column : rows) {
            var obj = toConvertData(column);
            var tuple = Tuple.wrap(Arrays.asList(obj));
            tuples.add(tuple);
        }

        var promise = Promise.promise();

        var future = client.preparedBatch(sql, tuples);
        future.onSuccess(rowSet -> {
            rowSet.property(MySQLClient.LAST_INSERTED_ID);
            promise.complete((long) rows.size());
        });
        future.onFailure(promise::fail);

        return promise.future();
    }


    private String insertSql(List<TableColumnMeta> metas, String table) {
        var tableName = SQLHelper.escapeMysqlField(table);
        var sb = new StringBuilder("INSERT INTO ");
        var vs = new StringBuilder();
        sb.append(tableName);
        sb.append("(");
        for (int i = 0; i < metas.size(); i++) {
            var meta = metas.get(i);
            var field = SQLHelper.escapeMysqlField(meta.getField());
            sb.append(field);
            vs.append("?");
            if (i < metas.size() - 1) {
                sb.append(",");
                vs.append(",");
            }
        }
        sb.append(") ");
        sb.append(" VALUES(");
        sb.append(vs.toString());
        sb.append(")");
        return sb.toString();
    }

    public String updateSql(List<TableColumnMeta> metas, TableColumnMeta keyMeta, String table) {
        var tableName = SQLHelper.escapeMysqlField(table);
        var sb = new StringBuilder("UPDATE ");
        sb.append(tableName);
        sb.append(" SET ");
        for (int i = 0; i < metas.size(); i++) {
            var meta = metas.get(i);
            var field = SQLHelper.escapeMysqlField(meta.getField());
            sb.append(field);
            sb.append("=?");
            if (i != metas.size() - 1) {
                sb.append(",");
            }
        }
        var keyField = SQLHelper.escapeMysqlField(keyMeta.getField());
        sb.append(" WHERE ");
        sb.append(keyField);
        sb.append("=?");
        return sb.toString();
    }

    private Object[] toConvertData(Object[] data) {
        for (int i = 0; i < data.length; i++) {
            var item = data[i];
            if (item != null && item.equals(NULL)) {
                data[i] = null;
            }
        }
        return data;
    }

    @Override
    public Future<Integer> batchDelete(TableColumnMeta keyMeta, Object[] keyValues, String table) {
        var sb = new StringBuilder();
        var tableName = SQLHelper.escapeMysqlField(table);
        sb.append("DELETE FROM ");
        sb.append(tableName);
        sb.append(" WHERE ");
        sb.append(SQLHelper.escapeMysqlField(keyMeta.getField()));
        sb.append(" =?");
        var sql = sb.toString();
        var tuples = new ArrayList<Tuple>();
        for (var keyValue : keyValues) {
            tuples.add(Tuple.of(keyValue));
        }
        var promise = Promise.<Integer>promise();
        var future = client.preparedBatch(sql, tuples);
        future.onSuccess(rows -> promise.complete(keyValues.length));
        future.onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Integer> executeSqlUpdate(String sql) {
        var promise = Promise.<Integer>promise();
        var future = client.query(sql);
        future.onSuccess(rows -> {
            promise.complete(rows.rowCount());
        });
        future.onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Integer> renameTable(String table, String target, String scheme) {
        var t = scheme + "." + table;
        var tt = scheme + "." + target;
        var sql = "rename table " + SQLHelper.escapeMysqlField(t) + " to " + SQLHelper.escapeMysqlField(tt);
        var promise = Promise.<Integer>promise();
        var future = client.query(sql);
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                promise.complete(ar.result().rowCount());
            } else {
                promise.fail(ar.cause());
            }
        });
        return promise.future();
    }

    /**
     * Get auto increment field
     */
    @Override
    public Optional<TableColumnMeta> getAutoIncreaseField(List<TableColumnMeta> metas) {
        return metas.stream().filter(TableColumnMeta::getAutoIncrement).findAny();
//        var optional = Optional.<TableColumnMeta>empty();
//        for (var tableColumnMeta : metas) {
//            if (tableColumnMeta.getExtra().contains("auto_increment")) {
//                optional = Optional.of(tableColumnMeta);
//                break;
//            }
//        }
//        return optional;
    }
}

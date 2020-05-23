package com.openjfx.database.mysql.impl;

import com.openjfx.database.DML;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import com.openjfx.database.mysql.SQLHelper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.util.*;
import java.util.stream.Collectors;

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
            for (Map<String, Object[]> item : items) {
                Object[] t = item.get(ROW);
                Object[] tt = toConvertData(t);
                Tuple tuple = Tuple.tuple();
                for (Object o : tt) {
                    tuple.addValue(o);
                }
                tuple.addValue(item.get(KEY)[0]);
                tuples.add(tuple);
            }

            client.preparedBatch(sql, tuples)
                    .onSuccess(r -> promise.complete(1))
                    .onFailure(promise::fail);
        }

        return promise.future();
    }

    @Override
    public Future<Long> insert(List<TableColumnMeta> metas, Object[] columns, String tableName) {

        var sql = insertSql(metas, tableName);

        var promise = Promise.<Long>promise();

        var tuple = Tuple.wrap(Arrays.asList(columns));

        client.preparedQuery(sql, tuple).onSuccess(rows -> {
            Long lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
            promise.complete(lastInsertId);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Object> batchInsert(List<TableColumnMeta> metas, List<Object[]> rows, String tableName) {
        var sql = insertSql(metas, tableName);
        var tuples = new ArrayList<Tuple>();

        for (Object[] column : rows) {
            Object[] obj = toConvertData(column);
            Tuple tuple = Tuple.wrap(Arrays.asList(obj));
            tuples.add(tuple);
        }

        Promise<Object> promise = Promise.promise();

        Future<RowSet<Row>> future = client.preparedBatch(sql, tuples);
        future.onSuccess(rowSet -> {
            rowSet.property(MySQLClient.LAST_INSERTED_ID);
            promise.complete((long) rows.size());
        });
        future.onFailure(promise::fail);

        return promise.future();
    }


    private String insertSql(List<TableColumnMeta> metas, String table) {
        var tableName = SQLHelper.escapeTableName(table);
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        StringBuilder vs = new StringBuilder();
        sb.append(tableName);
        sb.append("(");
        for (int i = 0; i < metas.size(); i++) {
            TableColumnMeta meta = metas.get(i);
            sb.append(meta.getField());
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
        var tableName = SQLHelper.escapeTableName(table);
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(tableName);
        sb.append(" SET ");
        for (int i = 0; i < metas.size(); i++) {
            TableColumnMeta meta = metas.get(i);
            sb.append(meta.getField());
            sb.append("=?");
            if (i != metas.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(" WHERE ");
        sb.append(keyMeta.getField());
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
        var tableName = SQLHelper.escapeTableName(table);
        sb.append("DELETE FROM ");
        sb.append(tableName);
        sb.append(" WHERE ");
        sb.append(keyMeta.getField());
        sb.append(" =?");
        String sql = sb.toString();
        List<Tuple> tuples = new ArrayList<>();
        for (Object keyValue : keyValues) {
            tuples.add(Tuple.of(keyValue));
        }
        Promise<Integer> promise = Promise.promise();
        var future = client.preparedBatch(sql, tuples);
        future.onSuccess(rows -> {
            promise.complete(keyValues.length);
        });
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

    /**
     * Get auto increment field
     */
    @Override
    public Optional<TableColumnMeta> getAutoIncreaseField(List<TableColumnMeta> metas) {
        var optional = Optional.<TableColumnMeta>empty();
        for (TableColumnMeta tableColumnMeta : metas) {
            if (tableColumnMeta.getExtra().contains("auto_increment")) {
                optional = Optional.of(tableColumnMeta);
                break;
            }
        }
        return optional;
    }
}

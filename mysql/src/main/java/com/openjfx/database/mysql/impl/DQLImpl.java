package com.openjfx.database.mysql.impl;

import com.openjfx.database.DQL;
import com.openjfx.database.model.TableColumnMeta;
import com.openjfx.database.mysql.PageHelper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.openjfx.database.common.config.StringConstants.NULL;

public class DQLImpl implements DQL {

    private MySQLPool client;

    public DQLImpl(MySQLPool client) {
        this.client = client;
    }

    @Override
    public Future<List<String>> showDatabase() {
        String sql = "SHOW DATABASES";
        Promise<List<String>> promise = Promise.promise();
        client.query(sql).onSuccess(r -> {
            List<String> schemes = new ArrayList<>();
            r.forEach(row -> {
                String scheme = row.getString(0);
                schemes.add(scheme);
            });
            promise.complete(schemes);
        }).onFailure(promise::fail);

        return promise.future();
    }

    @Override
    public Future<List<String>> showTables(String scheme) {
        String sql = "SHOW TABLES FROM " + scheme + "";
        Promise<List<String>> promise = Promise.promise();
        client.query(sql).onSuccess(r -> {
            List<String> schemes = new ArrayList<>();
            r.forEach(row -> {
                String table = row.getString(0);
                schemes.add(table);
            });
            promise.complete(schemes);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<List<TableColumnMeta>> showColumns(String table) {
        String sql = "SHOW FULL COLUMNS FROM " + table;

        Promise<List<TableColumnMeta>> promise = Promise.promise();

        client.query(sql)
                .onSuccess(rows -> {
                    List<TableColumnMeta> metas = new ArrayList<>();
                    for (Row row : rows) {
                        TableColumnMeta meta = new TableColumnMeta();
                        meta.setField(row.getString("Field"));
                        meta.setType(row.getString("Type"));
                        meta.setCollation(row.getString("Collation"));
                        meta.setNull(row.getString("Null"));
                        meta.setKey(row.getString("Key"));
                        meta.setDefault(row.getString("Default"));
                        meta.setExtra(row.getString("Extra"));
                        meta.setPrivileges(row.getString("Privileges"));
                        meta.setComment(row.getString("Comment"));
                        metas.add(meta);
                    }
                    promise.complete(metas);
                }).onFailure(promise::fail);

        return promise.future();
    }

    @Override
    public Future<List<Object[]>> query(String table, int pageIndex, int pageSize) {
        String sql = "SELECT * FROM " + table + " LIMIT ?,?";
        int a = PageHelper.getInitPage(pageIndex, pageSize);
        Tuple tuple = Tuple.of(a, pageSize);
        Promise<List<Object[]>> promise = Promise.promise();
        client.preparedQuery(sql, tuple).onSuccess(rows -> {
            List<Object[]> dd = new ArrayList<>();
            for (Row row : rows) {
                int size = row.size();
                Object[] obj = new Object[size];
                for (int i = 0; i < size; i++) {
                    Object b = row.getValue(i);
                    obj[i] = Objects.isNull(b)?NULL:b;
                }
                dd.add(obj);
            }
            promise.complete(dd);
        }).onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Long> count(String tableName) {
        String sql = "SELECT COUNT(*) FROM "+tableName;
        Promise<Long> promise = Promise.promise();
        client.query(sql).onSuccess(rows->{
            Long number = 0L;
            for (Row row : rows) {
                number = row.getLong(0);
            }
            promise.complete(number);
        }).onFailure(promise::fail);
        return promise.future();
    }
}

package com.openjfx.database.mysql.impl;

import com.openjfx.database.DDL;
import com.openjfx.database.mysql.SQLHelper;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLPool;

public class DDLImpl implements DDL {
    private MySQLPool client;

    public DDLImpl(MySQLPool client) {
        this.client = client;
    }

    @Override
    public Future<Void> dropDatabase(String database) {
        var sql = "DROP DATABASE " + database;
        var promise = Promise.<Void>promise();
        var future = client.query(sql);
        future.onSuccess(rows -> promise.complete());
        future.onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Void> dropTable(String table) {
        var tableName = SQLHelper.escapeMysqlField(table);
        var sql = "DROP TABLE " + tableName;
        var promise = Promise.<Void>promise();
        var future = client.query(sql);
        future.onSuccess(rows -> promise.complete());
        future.onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<String> ddl(String table) {
        var tableName = SQLHelper.escapeMysqlField(table);
        var sql = "SHOW CREATE TABLE " + tableName;
        var promise = Promise.<String>promise();
        var future = client.query(sql);
        future.onSuccess(rows -> {
            var ddl = "";
            for (var row : rows) {
                ddl = (String) row.getValue(1);
            }
            promise.complete(ddl);
        });
        future.onFailure(promise::fail);
        return promise.future();
    }

    @Override
    public Future<Integer> dropView(String view) {
        var viewName = SQLHelper.escapeMysqlField(view);
        var sql = "DROP VIEW IF EXISTS " + viewName;
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
}

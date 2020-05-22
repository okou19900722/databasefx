package com.openjfx.database.mysql.impl;

import com.openjfx.database.base.AbstractDataBasePool;
import io.vertx.mysqlclient.MySQLPool;

/**
 * Encapsulate mysql pool class
 *
 * @author yangkui
 * @since 1.0
 */
public class MysqlPoolImpl extends AbstractDataBasePool {

    private MysqlPoolImpl(MySQLPool pool) {
        this.pool = pool;
        dql = new DQLImpl(pool);
        ddl = new DDLImpl(pool);
        dml = new DMLImpl(pool);
        dataConvert = new SimpleMysqlDataConvert();
    }


    public static AbstractDataBasePool create(MySQLPool pool) {
        return new MysqlPoolImpl(pool);
    }
}

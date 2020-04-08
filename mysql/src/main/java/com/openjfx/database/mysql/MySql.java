package com.openjfx.database.mysql;

import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.base.AbstractDatabaseSource;
import com.openjfx.database.model.ConnectionParam;

import com.openjfx.database.mysql.impl.MysqlPoolImpl;
import io.vertx.mysqlclient.MySQLPool;

import java.util.Objects;

/**
 * mysql数据库连接池管理
 *
 * @author yangkui
 * @since 1.0
 */
public class MySql extends AbstractDatabaseSource {


    @Override
    public AbstractDataBasePool createPool(ConnectionParam params) {
        MySQLPool mySqlPool = MysqlHelper.createPool(params);
        AbstractDataBasePool pool = MysqlPoolImpl.create(mySqlPool);
        pools.put(params.getUuid(), pool);
        return pool;
    }

    @Override
    public void close(String uuid) {
        AbstractDataBasePool pool = pools.get(uuid);
        if (Objects.nonNull(pool)) {
            pool.close();
        }
        //移出数据库连接缓存
        pools.remove(uuid);
    }

    @Override
    public void closeAll() {
        pools.forEach((key, pool) -> {
            pool.close();
        });
        //清空数据库缓存
        pools.clear();
    }
}

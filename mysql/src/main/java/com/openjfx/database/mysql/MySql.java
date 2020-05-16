package com.openjfx.database.mysql;

import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.base.AbstractDatabaseSource;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;

import com.openjfx.database.mysql.impl.MysqlPoolImpl;
import io.vertx.core.json.JsonObject;


/**
 * mysql数据库连接池管理
 *
 * @author yangkui
 * @since 1.0
 */
public class MySql extends AbstractDatabaseSource {

    public MySql() {
        heartBeat();
    }

    @Override
    public AbstractDataBasePool createPool(ConnectionParam param) {
        var mySqlPool = MysqlHelper.createPool(param);
        var pool = MysqlPoolImpl.create(mySqlPool);
        pool.setConnectionParam(param);
        _createPool(pool, param);
        return pool;
    }

    @Override
    public AbstractDataBasePool createPool(ConnectionParam param, String uuid, String database, int initPoolSize) {
        var newParam = JsonObject.mapFrom(param).mapTo(ConnectionParam.class);
        newParam.setUuid(uuid);
        var mySqlPool = MysqlHelper.createPool(param, initPoolSize, database);
        var pool = MysqlPoolImpl.create(mySqlPool);
        _createPool(pool, newParam);
        return pool;
    }

    @Override
    public void heartBeat() {
        //每隔20s向mysql服务器发送一次sql查询语句
        VertexUtils.getVertex().setPeriodic(20000, timer -> {
            pools.forEach((a, b) -> {
                var future = b.getDql().heartBeatQuery();
                var serverName = b.getConnectionParam().getName();
                var host = b.getConnectionParam().getHost();
                var target = serverName + "<" + host + ">";
                future.onFailure(t -> LOGGER.error("failed heart beat to {} server cause:{}", target, t.getMessage()));
            });
        });
    }
}

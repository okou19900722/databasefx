package com.openjfx.database.base;

import com.openjfx.database.DataCharset;
import com.openjfx.database.DataType;
import com.openjfx.database.SQLGenerator;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.sqlclient.Pool;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database connection pool superclass
 *
 * @author yangkui
 * @since 1.0
 */
public abstract class AbstractDatabaseSource {
    /**
     * Database connection pool cache map
     */
    protected ConcurrentHashMap<String, AbstractDataBasePool> pools = new ConcurrentHashMap<>();
    /**
     * Heartbeat ID
     */
    protected Long timerId;
    /**
     * data charset
     */
    protected DataCharset charset;
    /**
     * current database support data type
     */
    protected DataType dataType;

    /**
     * current database local sql generator
     */
    protected SQLGenerator generator;

    /**
     * Get database connection pool according to UUID
     *
     * @param uuid uuid
     * @return Back to database connection pool
     */
    public AbstractDataBasePool getDataBaseSource(String uuid) {
        Objects.requireNonNull(uuid);
        return pools.get(uuid);
    }

    /**
     * New database connection pool
     *
     * @param params Connection parameters
     * @return Return to pool
     */
    public abstract AbstractDataBasePool createPool(ConnectionParam params);

    /**
     * Create database connection pool
     *
     * @param param        Create database connection pool connection parameters
     * @param uuid         Connection identification
     * @param initPoolSize Initialize dimensions
     * @return Back to connection pool
     */
    public abstract AbstractDataBasePool createPool(ConnectionParam param, String uuid, String database, int initPoolSize);

    /**
     * Create database connection pool
     *
     * @param param        connection param
     * @param uuid         Connection identification
     * @param initPoolSize Initialize dimensions
     * @return Back to connection pool
     */
    public abstract AbstractDataBasePool createPool(ConnectionParam param, String uuid, int initPoolSize);

    /**
     * Close a connection pool
     *
     * @param uuid uuid
     */
    public void close(String uuid) {
        var pool = pools.get(uuid);
        if (pool == null) {
            return;
        }
        pool.close();
        //Move out of database connection cache
        pools.remove(uuid);
        var param = pool.getConnectionParam();
        System.out.println(
                "================Connection pool is closed===============\r\n" +
                        "uuid:" + param.getUuid() + "\r\n" +
                        "connection name:" + param.getName() + "\r\n" +
                        "host:" + param.getHost() + "\r\n" +
                        "========================================================");
    }

    /**
     * close resource
     */
    public void closeAll() {
        pools.forEach((uuid, pool) -> close(uuid));
        //close timer
        if (timerId != null) {
            VertexUtils.getVertex().cancelTimer(timerId);
        }
    }

    /**
     * Prevent the database server from losing response without interaction for a long time
     */
    public abstract void heartBeat();

    /**
     * create pool after must check current pool can use,else call {@link Pool#close()}
     * close current database source pool
     *
     * @param pool  wait test database pool
     * @param param connection param
     */
    protected void _createPool(AbstractDataBasePool pool, ConnectionParam param) {
        pool.setConnectionParam(param);
        //Make sure the link is available before joining the cache
        var fut = pool.getDql().heartBeatQuery();
        fut.onSuccess(r -> {
            pools.put(param.getUuid(), pool);
            System.out.println(
                    "================Connection pool was created===============\r\n" +
                            "uuid:" + param.getUuid() + "\r\n" +
                            "connection name:" + param.getName() + "\r\n" +
                            "host:" + param.getHost() + "\r\n" +
                            "========================================================="
            );
        });
        fut.onFailure(t -> {
            pool.close();
            System.out.println(
                    "================Connection pool create failed===============\r\n" +
                            "uuid:" + param.getUuid() + "\r\n" +
                            "connection name:" + param.getName() + "\r\n" +
                            "host:" + param.getHost() + "\r\n" +
                            "failed cause:" + t.getMessage() + "\r\n" +
                            "========================================================="
            );
        });
    }

    public DataCharset getCharset() {
        return charset;
    }

    public DataType getDataType() {
        return dataType;
    }

    public SQLGenerator getGenerator() {
        return generator;
    }
}

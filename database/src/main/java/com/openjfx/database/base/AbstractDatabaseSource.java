package com.openjfx.database.base;

import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database connection pool superclass
 *
 * @author yangkui
 * @since 1.0
 */
public abstract class AbstractDatabaseSource {
    protected final static Logger LOGGER = LogManager.getLogger();
    /**
     * Database connection pool cache map
     */
    protected ConcurrentHashMap<String, AbstractDataBasePool> pools = new ConcurrentHashMap<>();
    /**
     * Heartbeat ID
     */
    protected Long timerId;

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
     * Close a connection pool
     *
     * @param uuid uuid
     */
    public void close(String uuid) {
        var pool = pools.get(uuid);
        if (Objects.nonNull(pool)) {
            pool.close();
            LOGGER.info("remove database pool:{}", uuid);
        }
        //Move out of database connection cache
        pools.remove(uuid);
    }

    /**
     * close resource
     */
    public void closeAll() {
        pools.forEach((key, pool) -> close(key));
        //close timer
        if (timerId != null) {
            VertexUtils.getVertex().cancelTimer(timerId);
        }
    }

    /**
     * Prevent the database server from losing response without interaction for a long time
     */
    public abstract void heartBeat();

    protected void _createPool(AbstractDataBasePool pool, ConnectionParam param) {
        pool.setConnectionParam(param);
        //Make sure the link is available before joining the cache
        var fut = pool.getDql().heartBeatQuery();
        fut.onSuccess(r -> pools.put(param.getUuid(), pool));
        fut.onFailure(t -> {
            LOGGER.error(t.getMessage(), t);
            pool.close();
        });
    }
}

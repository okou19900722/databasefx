package com.openjfx.database.mysql;

import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.ConnectionParam;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.List;
import java.util.Optional;


/**
 * Some auxiliary methods of MySQL database operation
 *
 * @author yangkui
 * @since 1.0
 */
public class MysqlHelper {

    /**
     * Create database connection pool
     *
     * @param param parameter
     * @return Back to database connection pool
     */
    public static MySQLPool createPool(ConnectionParam param) {
        return createPool(param, 10);
    }

    /**
     * Create database connection pool
     *
     * @param param    Connection parameters
     * @param initSize Initialize dimensions
     * @return Back to connection pool
     */
    public static MySQLPool createPool(ConnectionParam param, int initSize) {
        return createPool(param, initSize, null);
    }

    /**
     * Create database connection pool
     *
     * @param param    Connection parameters
     * @param initSize Initialize connection pool size
     * @param database Initialize database
     * @return Back to connection pool
     */
    public static MySQLPool createPool(ConnectionParam param, int initSize, String database) {
        var options = new MySQLConnectOptions()
                .setPort(param.getPort())
                .setHost(param.getHost())
                .setUser(param.getUser())
                .setPassword(param.getPassword())
                .setTcpKeepAlive(true)
                //Set link timeout to 5s
                .setConnectTimeout(5000)
                .setIdleTimeout(5)
                .setSslHandshakeTimeout(5);

        if (StringUtils.nonEmpty(database)) {
            options.setDatabase(database);
        }

        var poolOptions = new PoolOptions();
        poolOptions.setMaxSize(initSize);
        return MySQLPool.pool(options, poolOptions);
    }

    /**
     * test connection
     *
     * @param param con param
     * @return async return test result
     */
    public static Future<Boolean> testConnection(ConnectionParam param) {
        var client = createPool(param);
        var testSql = "SELECT 1";
        var promise = Promise.<Boolean>promise();
        var future = client.query(testSql);
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                promise.complete(true);
            } else {
                promise.fail(ar.cause());
            }
            client.close();
        });
        return promise.future();
    }

    /**
     * Get primary key from a table
     *
     * @param metas table column list
     * @return primary key
     */
    public static Optional<TableColumnMeta> getPrimaryKey(List<TableColumnMeta> metas) {
        return metas.stream().filter(TableColumnMeta::getPrimaryKey).findAny();
    }

}

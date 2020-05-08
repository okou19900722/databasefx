package com.openjfx.database.base;

import com.openjfx.database.*;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnection;

/**
 * 封装数据库管理连接池
 *
 * @author yangkui
 * @since 1.0
 */
public class AbstractDataBasePool {
    /*********************************
     *          数据库语言             *
     *********************************/
    protected DDL ddl;
    protected DCL dcl;
    protected DML dml;
    protected DQL dql;

    protected SQLGenerator generator;

    protected Pool pool;

    public DDL getDdl() {
        return ddl;
    }

    public void setDdl(DDL ddl) {
        this.ddl = ddl;
    }

    public DCL getDcl() {
        return dcl;
    }

    public void setDcl(DCL dcl) {
        this.dcl = dcl;
    }

    public DML getDml() {
        return dml;
    }

    public void setDml(DML dml) {
        this.dml = dml;
    }

    public DQL getDql() {
        return dql;
    }

    public void setDql(DQL dql) {
        this.dql = dql;
    }

    public void close() {
        pool.close();
    }

    public SQLGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(SQLGenerator generator) {
        this.generator = generator;
    }

    /**
     * get connection from database pool.
     * <p>This method will not be used in general,
     * but it needs to be called when some scenes only need to get the linked object to complete.
     * Such as database things, etc</p>
     *
     * @return connection
     */
    public Future<SqlConnection> getConnection() {
        return pool.getConnection();
    }
}

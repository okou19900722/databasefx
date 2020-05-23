package com.openjfx.database.mysql.impl;

import com.openjfx.database.SQLGenerator;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.mysqlclient.MySQLPool;

import java.util.List;

/**
 * Mysql Generator impl
 *
 * @author yangkui
 * @since 1.0
 */
public class SQLGeneratorImpl implements SQLGenerator {
    @Override
    public String createScheme(String name, String charset, String collation) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        var sql = "CREATE DATABASE `" + name + "`";
        if (StringUtils.nonEmpty(charset)) {
            sql += " CHARACTER SET '" + charset + "'";
        }
        if (StringUtils.nonEmpty(collation)) {
            sql += " COLLATE '" + collation + "'";
        }
        return sql;
    }
}

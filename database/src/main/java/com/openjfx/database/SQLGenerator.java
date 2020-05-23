package com.openjfx.database;


/**
 * SQL statement generation interface
 *
 * @author yangkui
 * @since 1.0
 */
public interface SQLGenerator {

    /**
     * current database create scheme
     *
     * @param name      scheme name
     * @param charset   scheme charset
     * @param collation scheme charset collation
     * @return create scheme sql
     */
    String createScheme(String name, String charset, String collation);
}

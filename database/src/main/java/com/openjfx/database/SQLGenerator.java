package com.openjfx.database;


import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;

import java.util.List;

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

    /**
     * Create SQL statement to modify table field information
     *
     * @param table        target table
     * @param changeModels change model
     * @param metas        table column meta
     * @return sql statement
     */
    String createFieldModifySqlStatement(String table, List<RowChangeModel> changeModels, List<TableColumnMeta> metas);

    /**
     * create table
     *
     * @param table        table name
     * @param changeModels row list
     * @return create table sql
     */
    String createTable(String table, List<RowChangeModel> changeModels);

    /**
     * generate select sql
     *
     * @param metas table column meta
     * @return sql statement
     */
    String select(List<TableColumnMeta> metas, String table);
}

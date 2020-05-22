package com.openjfx.database;

import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.concurrent.Future;

/**
 * sql生成接口
 *
 * @author yangkui
 * @since 1.0
 */
public interface SQLGenerator {
    /**
     * SELECT
     *
     * @param metas
     * @param tableName
     * @return
     */
    String select(List<TableColumnMeta> metas, String tableName);

    /**
     * UPDATE
     *
     * @param metas
     * @param tableName
     * @return
     */
    String update(List<TableColumnMeta> metas, String tableName);

    /**
     * INSERT
     *
     * @param metas
     * @param tableName
     * @return
     */
    String insert(List<TableColumnMeta> metas, String tableName);

    /**
     * DELETE
     *
     * @param metas
     * @param tableName
     * @return
     */
    String delete(List<TableColumnMeta> metas, String tableName);

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

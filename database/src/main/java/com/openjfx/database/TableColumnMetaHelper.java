package com.openjfx.database;

import com.openjfx.database.model.TableColumnMeta;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Table Column meta data
 *
 * @author yangkui
 * @since 1.0
 */
public class TableColumnMetaHelper {
    /**
     * 获取一张表的主键
     *
     * @param metas 表列集合
     * @return 返回列信息
     */
    public static Optional<TableColumnMeta> getTableKey(List<TableColumnMeta> metas) {
        return metas.stream()
                .filter(meta -> Objects.nonNull(meta.getKey()))
                .findAny();
    }
}

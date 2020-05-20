package com.openjfx.database;

import java.util.List;

/**
 * @author yangkui
 * @since 1.0
 */
public interface DataType {
    /**
     * obtain current database support data type
     *
     * @return return data type list
     */
    List<String> getDataTypeList();
}

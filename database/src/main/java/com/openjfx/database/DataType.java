package com.openjfx.database;

import com.openjfx.database.model.TableColumnMeta;

import java.util.List;

/**
 * @author yangkui
 * @since 1.0
 */
public interface DataType {
    enum DataTypeEnum {
        NUMBER,
        DATETIME,
        STRING
    }

    /**
     * obtain current database support data type
     *
     * @return return data type list
     */
    List<String> getDataTypeList();

    /**
     * according by {@link TableColumnMeta#getType()} get data type
     *
     * @param type data type
     * @return return type string
     */
    String getDataType(final String type);

    /**
     * according by {@link TableColumnMeta#getType()} get data length
     *
     * @param type data type
     * @return return data type
     */
    int getDataTypeLength(final String type);

    /**
     * Judge whether a type is classified
     *
     * @param typeName     target type name
     * @param dataTypeEnum target category
     * @return If the input target classification returns true, otherwise it returns false
     */
    boolean isCategory(String typeName, DataTypeEnum dataTypeEnum);
}

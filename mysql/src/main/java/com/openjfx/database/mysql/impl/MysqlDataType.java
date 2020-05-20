package com.openjfx.database.mysql.impl;

import com.openjfx.database.DataType;
import com.openjfx.database.common.VertexUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * mysql data type
 *
 * @author yangkui
 * @since 1.0
 */
public class MysqlDataType implements DataType {

    private final static List<String> DATA_TYPE = new ArrayList<>();

    static {
        var fs = VertexUtils.getFileSystem();
        var buffer = fs.readFileBlocking("database/data_type.json");
        var array = buffer.toJsonArray();
        //order by charset name first letter
        for (Object o : array) {
            var type = (String) o;
            DATA_TYPE.add(type);
        }
    }

    @Override
    public List<String> getDataTypeList() {
        return DATA_TYPE;
    }
}

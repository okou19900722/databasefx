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
    public String getDataType(String type) {
        var index = type.indexOf("(");
        var tty = type;
        if (index != -1) {
            tty = type.substring(0, index);
        }
        return tty;
    }

    @Override
    public int getDataTypeLength(String type) {
        var index = type.indexOf("(");
        var length = "0";
        if (index != -1) {
            length = type.substring(index + 1, type.length() - 1);
        }
        return Integer.parseInt(length);
    }

    @Override
    public List<String> getDataTypeList() {
        return DATA_TYPE;
    }
}

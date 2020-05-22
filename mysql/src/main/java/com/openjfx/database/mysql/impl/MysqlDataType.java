package com.openjfx.database.mysql.impl;

import com.openjfx.database.DataType;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.DataTypeModel;
import io.vertx.core.json.JsonObject;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mysql data type
 *
 * @author yangkui
 * @since 1.0
 */
public class MysqlDataType implements DataType {

    private final static List<DataTypeModel> DATA_TYPE = new ArrayList<>();

    static {
        var fs = VertexUtils.getFileSystem();
        var buffer = fs.readFileBlocking("database/data_type.json");
        var array = buffer.toJsonArray();
        //order by charset name first letter
        for (Object o : array) {
            var type = (JsonObject) o;
            var model = type.mapTo(DataTypeModel.class);
            DATA_TYPE.add(model);
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
    public boolean isCategory(String typeName, DataTypeEnum dataTypeEnum) {
        boolean result = false;
        var _typeName = typeName.toUpperCase();
        var optional = DATA_TYPE.stream()
                .filter(dataTypeModel -> dataTypeModel.getTypeName().equals(_typeName)).findAny();
        if (optional.isPresent()) {
            var dataTypeModel = optional.get();
            var category = dataTypeModel.getCategory();
            result = category.equals(dataTypeEnum.toString());
        }
        return result;
    }

    @Override
    public List<String> getDataTypeList() {
        return DATA_TYPE.stream()
                .map(DataTypeModel::getTypeName)
                .map(String::toLowerCase).collect(Collectors.toList());
    }
}

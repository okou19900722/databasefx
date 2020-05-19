package com.openjfx.database.mysql.impl;

import com.openjfx.database.DataCharset;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.DatabaseCharsetModel;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * msql charset
 *
 * @author yangkui
 * @since 1.0
 */
public class MysqlCharset implements DataCharset {

    private static final List<DatabaseCharsetModel> CHARSETS = new ArrayList<>();

    static {
        var fs = VertexUtils.getFileSystem();
        var buffer = fs.readFileBlocking("database/charset.json");
        var array = buffer.toJsonArray();
        //order by charset name first letter
        for (Object o : array) {
            var json = (JsonObject) o;
            var charset = json.mapTo(DatabaseCharsetModel.class);
            CHARSETS.add(charset);
        }
    }

    @Override
    public int getCharsetLength(String charset) {
        return 0;
    }

    @Override
    public List<DatabaseCharsetModel> getDatabaseCharset() {
        return CHARSETS;
    }

    @Override
    public List<String> getCharsetCollations(String charsetName) {
        for (DatabaseCharsetModel model : CHARSETS) {
            if (model.getCharset().equals(charsetName)) {
                return model.getCollations();
            }
        }
        return List.of();
    }
}

package com.openjfx.database.mysql;

/**
 * SQL statement processing auxiliary class
 *
 * @author yangkui
 * @since 1.0
 */
public class SQLHelper {
    /**
     * Escape MySQL table name
     *
     * @param tableName Target table name
     * @return escape after table name
     */
    public static String escapeTableName(String tableName) {
        var array = tableName.split("\\.");
        var stringBuffer = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            var s = array[i];
            stringBuffer.append("`").append(s).append("`");
            if (i != array.length - 1) {
                stringBuffer.append(".");
            }
        }
        return stringBuffer.toString();
    }
}

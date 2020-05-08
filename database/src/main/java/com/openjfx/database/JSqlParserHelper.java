package com.openjfx.database;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.util.TablesNamesFinder;

import java.util.List;

/**
 * JSsqlParser 辅助工具类
 *
 * @author yangkui
 * @since 1.0
 */
public class JSqlParserHelper {
    /**
     * 查询一条sql语句中的所有表名
     *
     * @param sql 目标sql语句
     * @return 返回表明列表
     * @throws JSQLParserException sql解析异常
     */
    public static List<String> getTableName(final String sql) throws JSQLParserException {
        var statement = CCJSqlParserUtil.parse(sql);
//        var selectStatement = (Select) statement;
        var tablesNamesFinder = new TablesNamesFinder();
        return tablesNamesFinder.getTableList(statement);
    }

    /**
     * 将普通sql语句转换为全表名sql语句
     * @param sql sql语句
     * @param scheme 表scheme
     * @return 返回全称sql语句
     * @throws JSQLParserException 解析异常
     */
    public static String transformSqlToFullName(String sql, String scheme) throws JSQLParserException {
        var tables = getTableName(sql);
        for (String table : tables) {
            //说明表名已经加了数据库名称
            if (table.contains(".")) {
                continue;
            }
            var tName = scheme + "." + table;
            sql = sql.replaceAll(table, tName);
        }
        return sql;
    }
}

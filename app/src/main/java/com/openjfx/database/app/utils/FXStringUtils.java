package com.openjfx.database.app.utils;

/**'
 *
 *
 * FX 字符串处理工具类
 *
 * @author yangkui
 * @since 1.0
 *
 */
public class FXStringUtils {
    /**
     *
     *
     *
     * 获取表Tab识别码,生成规则 uuid+_+database+_+tableName
     * @param uuid 数据库连接标示uuid
     * @param database  所属数据库名称
     * @param tableName 表名
     * @return 返回表Tab识别码
     */
    public static String getTableTabUUID(String uuid,String database,String tableName){
        return uuid+"_"+database+"_"+tableName;
    }
}

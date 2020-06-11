package com.openjfx.database.common.utils;

/**
 * 系统相关操作工具类
 *
 * @author yangkui
 * @since 1.0
 */
public class OSUtils {
    /**
     * System OS type
     *
     * @author yangkui
     * @since 1.0
     */
    public enum OsType {
        /**
         * linux
         */
        LINUX("Linux"),
        /**
         * window
         */
        WINDOW("Window"),
        /**
         * mac
         */
        MAC("Mac");

        private final String osName;

        OsType(String osName) {
            this.osName = osName;
        }
    }

    /**
     * 获取操作系统名称
     *
     * @return 返回操作系统名称
     */
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    /**
     * 获取用户主目录
     *
     * @return 返回主目录路径
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }

}

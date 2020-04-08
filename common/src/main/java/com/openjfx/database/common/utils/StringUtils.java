package com.openjfx.database.common.utils;

import java.util.Objects;

/**
 * 字符串操作工具类
 *
 * @author yangkui
 * @since 1.0
 */
public class StringUtils {
    /**
     * 判断某个字符串是否为空
     *
     * @param target 目标字符串
     * @return 返回判断结果
     */
    public static boolean isEmpty(String target) {
        return Objects.isNull(target) || "".equals(target.trim());
    }

    /**
     * 判断目标字符串是否非空
     *
     * @param target 目标字符串
     * @return 返回判断结果
     */
    public static boolean nonEmpty(String target) {
        return !isEmpty(target);
    }
}

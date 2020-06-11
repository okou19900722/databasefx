package com.openjfx.database.common.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static String localDateTimeToStr(LocalDateTime dateTime, String pattern) {
        var format = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(format);
    }

    /**
     * obtain object str value {@link Object#toString()}
     *
     * @param obj          target object
     * @param defaultValue default value
     * @return str
     */
    public static String getObjectStrElseGet(Object obj, String defaultValue) {
        final String str;
        if (obj != null) {
            str = obj.toString();
        } else {
            str = defaultValue;
        }
        return str;
    }

    /**
     * obtain object str value {@link Object#toString()}
     *
     * @param obj          target object
     * @param defaultValue default value
     * @return str
     */
    public static String getObjectStrElseGet(Object obj, String defaultValue, String format) {
        final String str;
        if (obj instanceof LocalDateTime) {
            str = StringUtils.localDateTimeToStr((LocalDateTime) obj, format);
        } else {
            str = getObjectStrElseGet(obj, defaultValue);
        }
        return str;
    }
}

package com.openjfx.database.app.model;

/**
 * base tab model
 *
 * @author yangkui
 * @since 1.0
 */
public class BaseTabMode {
    /**
     * 数据库标识
     */
    protected final String uuid;
    /**
     * Tab 独立标识
     */
    protected final String flag;

    public BaseTabMode(String uuid, String flag) {
        this.uuid = uuid;
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }

    public String getUuid() {
        return uuid;
    }
}

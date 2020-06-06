package com.openjfx.database.app.model.tab.meta;

import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.model.tab.BaseTabMode;
import com.openjfx.database.common.utils.StringUtils;
import io.vertx.core.json.JsonObject;

/**
 * design table model
 *
 * @author yangkui
 * @since 1.0
 */
public class DesignTabModel extends BaseTabMode {
    /**
     * design table type
     *
     * @author yangkui
     * @since 1.0
     */
    public enum DesignTableType {
        /**
         * create design table
         */
        CREATE,
        /**
         * update design table
         */
        UPDATE
    }

    /**
     * design table type
     */
    private DesignTableType designTableType;

    /**
     * scheme
     */
    private String scheme;
    /**
     * table name
     */
    private String tableName;

    public DesignTabModel(String uuid, String flag) {
        super(uuid, flag);
    }

    public void setDesignTableType(DesignTableType designTableType) {
        this.designTableType = designTableType;
    }

    public DesignTableType getDesignTableType() {
        return designTableType;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getScheme() {
        return scheme;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public static DesignTabModel build(String uuid, String scheme, String tableName, DesignTableType designTableType) {
        var flag = uuid + "_" + scheme + "_design_" + (StringUtils.isEmpty(tableName) ? "UnTitle" : tableName);
        var model = new DesignTabModel(uuid, flag);
        model.setDesignTableType(designTableType);
        model.setTableName(tableName);
        model.setScheme(scheme);
        return model;
    }

    public static DesignTabModel build(JsonObject json) {
        var uuid = json.getString(Constants.UUID);
        var scheme = json.getString(Constants.SCHEME);
        var tableName = json.getString(Constants.TABLE_NAME);
        var type = json.getString(Constants.TYPE);
        return build(uuid, scheme, tableName, DesignTableType.valueOf(type));
    }
}

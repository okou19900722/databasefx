package com.openjfx.database.app.model.impl;

import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.controls.impl.TableTreeNode;
import com.openjfx.database.app.controls.impl.TableViewNode;
import com.openjfx.database.app.model.BaseTabMode;
import javafx.scene.control.TreeItem;

/**
 * tab数据
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTabModel extends BaseTabMode {
    /**
     * 数据库名称
     */
    private final String database;
    /**
     * 表名
     */
    private final String tableName;
    /**
     * 连接名称
     */
    private final String serverName;

    private TableType tableType;

    public TableTabModel(String serverName, String uuid, String database, String tableName) {
        super(uuid + "_" + database + "_" + tableName);
        this.uuid = uuid;
        this.database = database;
        this.tableName = tableName;
        this.serverName = serverName;
    }


    public static TableTabModel build(TreeItem<String> treeNode) {
        final TableTabModel model;
        if (treeNode instanceof TableTreeNode) {
            var tableNode = (TableTreeNode) treeNode;
            model = new TableTabModel(tableNode.getServerName(), tableNode.getUuid(), tableNode.getDatabase(), tableNode.getValue());
            model.setTableType(TableType.BASE_TABLE);
        } else {
            var viewNode = (TableViewNode) treeNode;
            model = new TableTabModel(viewNode.getServerName(), viewNode.getUuid(), viewNode.getDatabase(), viewNode.getValue());
            model.setTableType(TableType.VIEW);
        }
        return model;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return database + "." + tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getServerName() {
        return serverName;
    }

    public TableType getTableType() {
        return tableType;
    }

    public void setTableType(TableType tableType) {
        this.tableType = tableType;
    }

    /**
     * table type
     *
     * @author yangkui
     * @since 1.0
     */
    public static enum TableType {
        /**
         * system view
         */
        SYSTEM_VIEW,
        /**
         * base table
         */
        BASE_TABLE,
        /**
         * view
         */
        VIEW
    }
}

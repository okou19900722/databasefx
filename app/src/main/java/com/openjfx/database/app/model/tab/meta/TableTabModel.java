package com.openjfx.database.app.model.tab.meta;

import com.openjfx.database.app.controls.impl.TableTreeNode;
import com.openjfx.database.app.controls.impl.TableViewNode;
import com.openjfx.database.app.model.tab.BaseTabMode;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Table tab metadata
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTabModel extends BaseTabMode implements Initializable {
    /**
     * Database name
     */
    private final String database;
    /**
     * Table name
     */
    private final String tableName;
    /**
     * Server name
     */
    private final String serverName;
    /**
     * Table type
     */
    private TableType tableType;

    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
    }

    public TableTabModel(String uuid, String flag, String database, String tableName, String serverName, TableType tableType) {
        super(uuid, flag);
        this.database = database;
        this.tableName = tableName;
        this.serverName = serverName;
        this.tableType = tableType;
    }

    public static TableTabModel build(TreeItem<String> treeNode) {
        final String scheme;
        final String serverName;
        final String tableName;
        final TableType tableType;
        final String uuid;
        if (treeNode instanceof TableTreeNode) {
            var tableNode = (TableTreeNode) treeNode;
            scheme = tableNode.getScheme();
            serverName = tableNode.getServerName();
            tableName = tableNode.getValue();
            tableType = TableType.BASE_TABLE;
            uuid = tableNode.getUuid();
        } else {
            var viewNode = (TableViewNode) treeNode;
            scheme = viewNode.getScheme();
            serverName = viewNode.getServerName();
            tableName = viewNode.getValue();
            tableType = TableType.VIEW;
            uuid = viewNode.getUuid();
        }
        var flag = uuid + "_" + scheme + "_" + tableName;
        return new TableTabModel(uuid, flag, scheme, tableName, serverName, tableType);
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
    public enum TableType {
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

package com.openjfx.database.app.model.impl;

import com.openjfx.database.app.controls.impl.UserTreeNode;
import com.openjfx.database.app.model.BaseTabMode;

/**
 * user tab model
 *
 * @author yangkui
 * @since 1.0
 */
public class UserTabModel extends BaseTabMode {
    /**
     * user name
     */
    private String user;
    /**
     * host
     */
    private String host;
    /**
     * tab value
     */
    private String value;
    /**
     * server name
     */
    private String serverName;

    public UserTabModel(String uuid, String flag) {
        super(uuid, flag);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerName() {
        return serverName;
    }

    public static UserTabModel build(UserTreeNode treeNode) {
        var uuid = treeNode.getUuid();
        var value = treeNode.getValue();
        var flag = uuid + "_user_" + value;
        var tabModel = new UserTabModel(uuid, flag);
        var array = value.split("@");
        tabModel.setHost(array[1]);
        tabModel.setUser(array[0]);
        tabModel.setServerName(treeNode.getServerName());
        tabModel.setValue(value);
        return tabModel;
    }
}

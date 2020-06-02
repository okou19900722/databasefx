package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.model.ConnectionParam;
import javafx.scene.image.Image;

/**
 * user tree node
 *
 * @author yangkui
 * @since 1.0
 */
public class UserTreeNode extends BaseTreeNode<String> {

    private final static Image USER_ICON = AssetUtils.getLocalImage(20, 20, "user_icon.png");

    public UserTreeNode(ConnectionParam param, String user) {
        super(param, USER_ICON);
        setValue(user);
    }

    @Override
    public void init() {
    }
}

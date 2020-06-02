package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.ConnectionParam;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.ArrayList;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;

/**
 * User folder node
 *
 * @author yangkui
 * @since 1.0
 */
public class UserFolderNode extends BaseTreeNode<String> {
    private final static Image IMAGE = AssetUtils.getLocalImage(20, 20, "folder_icon.png");

    public UserFolderNode(ConnectionParam param) {
        super(param, IMAGE);
        setValue(I18N.getString("databasefx.tree.user.folder"));
        var flush = new MenuItem(I18N.getString("menu.databasefx.tree.flush"));
        flush.setOnAction(event -> flush());
        addMenuItem(flush);
    }

    @Override
    public void init() {
        if (getChildren().size() > 0 || isLoading()) {
            return;
        }
        var pool = DATABASE_SOURCE.getDataBaseSource(param.get().getUuid());
        var future = pool.getDql().getCurrentDatabaseUserList();
        future.onSuccess(list -> {
            var children = new ArrayList<UserTreeNode>();
            for (String[] strings : list) {
                var user = strings[1] + "@" + strings[0];
                var node = new UserTreeNode(param.get(), user);
                children.add(node);
            }
            Platform.runLater(() -> {
                getChildren().addAll(children);
                setExpanded(true);
            });
            setLoading(false);
        });
        future.onFailure(t -> initFailed(t, I18N.getString("databasefx.tree.user.load.fail")));
    }
}

package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.model.ConnectionParam;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;

/**
 * View folder
 *
 * @author yangkui
 * @since 1.0
 */
public class ViewFolderNode extends BaseTreeNode<String> {
    private final static Image VIEW_ICON = AssetUtils.getLocalImage(20, 20, "folder_icon.png");
    private final String scheme;

    public ViewFolderNode(ConnectionParam param, String scheme) {
        super(param, VIEW_ICON);
        this.scheme = scheme;
        setValue(I18N.getString("databasefx.tree.view.folder"));
    }

    @Override
    public void init() {
        if (getChildren().size() > 0 || isLoading()) {
            return;
        }
        setLoading(true);
        var pool = DATABASE_SOURCE.getDataBaseSource(getUuid());
        var future = pool.getDql().showViews(scheme);
        future.onComplete(ar -> {
            if (ar.succeeded()) {
                var rs = ar.result();
                var list = rs.stream().map(view -> new TableViewTreeNode(scheme, view, getParam())).collect(Collectors.toList());
                Platform.runLater(() -> {
                    getChildren().addAll(list);
                    setExpanded(true);
                });
            } else {
                initFailed(ar.cause(), I18N.getString("databasefx.tree.view.load.fail"));
            }
            setLoading(false);
        });
    }
}

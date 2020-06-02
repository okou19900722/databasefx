package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.DatabaseFX;
import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.stage.CreateSchemeStage;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.model.ConnectionParam;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.I18N;

/**
 * scheme folder
 *
 * @author yangkui
 * @since 1.0
 */
public class SchemeFolderNode extends BaseTreeNode<String> {

    private final static Image IMAGE = AssetUtils.getLocalImage(20, 20, "folder_icon.png");

    public SchemeFolderNode(ConnectionParam param) {
        super(param, IMAGE);
        setValue(I18N.getString("databasefx.tree.scheme.folder"));

        final var createScheme = new MenuItem(I18N.getString("menu.databasefx.tree.create.connection"));
        final var flush = new MenuItem(I18N.getString("menu.databasefx.tree.flush"));

        createScheme.setOnAction(event -> new CreateSchemeStage(getUuid()));
        flush.setOnAction((e) -> this.flush());

        addMenuItem(createScheme, flush);
    }

    @Override
    public void init() {
        if (getChildren().size() > 0 || isLoading()) {
            return;
        }
        setLoading(true);
        var pool = DatabaseFX.DATABASE_SOURCE.getDataBaseSource(param.get().getUuid());
        var future = pool.getDql().showDatabase();
        future.onSuccess(schemes -> {
            var schemeTreeNodes = schemes.stream().map(s -> new SchemeTreeNode(s, param.get())).collect(Collectors.toList());
            Platform.runLater(() -> {
                getChildren().addAll(schemeTreeNodes);
                setExpanded(true);
            });
            setLoading(false);
        });
        future.onFailure(t -> initFailed(t, I18N.getString("databasefx.tree.database.load.fail")));
    }
}

package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.stage.DesignTableStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * table folder node
 *
 * @author yangkui
 * @since 1.0
 */
public class TableFolderNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(20, 20, "folder_icon.png");
    private final String scheme;

    public TableFolderNode(ConnectionParam param, String scheme) {
        super(param, ICON_IMAGE);
        this.scheme = scheme;
        setValue(I18N.getString("database.table"));
        final var createTable = new MenuItem(I18N.getString("menu.databasefx.tree.create.table"));
        final var flush = new MenuItem(I18N.getString("menu.databasefx.tree.flush"));
        //flush table list
        flush.setOnAction((event) -> flush());
        //show create table stage
        createTable.setOnAction(event -> {
            var params = new JsonObject();
            params.put(Constants.UUID, getUuid());
            params.put(Constants.SCHEME, scheme);
            params.put(Constants.TYPE, 0);
            new DesignTableStage(params);
        });

        addMenuItem(flush, createTable);
    }

    @Override
    public void init() {
        if (getChildren().size() > 0 || isLoading()) {
            return;
        }
        setLoading(true);
        var dcl = DATABASE_SOURCE.getDataBaseSource(getUuid()).getDql();
        var future = dcl.showTables(scheme);
        future.onComplete(ar ->
        {
            if (ar.failed()) {
                DialogUtils.showErrorDialog(ar.cause(), I18N.getString("menu.databasefx.tree.database.init.fail"));
            } else {
                var tas = ar.result().stream().map(s -> new TableTreeNode(scheme, s, param.get())).collect(Collectors.toList());
                Platform.runLater(() -> {
                    getChildren().addAll(tas);
                    setExpanded(true);
                });
            }
            setLoading(false);
        });

    }
}

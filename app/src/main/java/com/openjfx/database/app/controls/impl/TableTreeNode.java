package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.DatabaseFX;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.stage.DesignTableStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;


import static com.openjfx.database.app.DatabaseFX.I18N;
import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * Database table node
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTreeNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(20, 20, "table_icon.png");

    /**
     * database
     */
    private final String database;

    public TableTreeNode(String database, String tableName, ConnectionParam param) {
        super(param, ICON_IMAGE);

        this.database = database;

        var params = new JsonObject();
        params.put(Constants.UUID, getUuid());
        params.put(SCHEME, database);
        params.put(TABLE_NAME, tableName);
        params.put(TYPE, 1);

        setValue(tableName);

        var design = new MenuItem(I18N.getString("menu.databasefx.tree.design.table"));
        var delete = new MenuItem(I18N.getString("menu.databasefx.tree.delete.table"));


        design.setOnAction(e -> new DesignTableStage(params));
        delete.setOnAction(e -> {
            var tips = I18N.getString("menu.databasefx.tree.delete.table.tips") + " " + tableName + "?";
            var result = DialogUtils.showAlertConfirm(tips);
            if (!result) {
                return;
            }
            var pool = DatabaseFX.DATABASE_SOURCE.getDataBaseSource(getUuid());
            var future = pool.getDdl().dropTable(database + "." + tableName);

            future.onSuccess(ar -> {
                var message = new JsonObject();
                var flag = getUuid() + "_" + database + "_" + tableName;
                message.put(ACTION, MainTabPane.EventBusAction.REMOVE);
                message.put(FLAG, flag);
                VertexUtils.eventBus().send(MainTabPane.EVENT_BUS_ADDRESS, message);
                Platform.runLater(() -> getParent().getChildren().remove(this));
            });

            future.onFailure(t -> DialogUtils.showErrorDialog(t, I18N.getString("menu.databasefx.tree.delete.table.fail")));
        });
        addMenuItem(design, delete);
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public void init() {
    }
}

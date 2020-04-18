package com.openjfx.database.app.component.impl;

import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.component.BaseTreeNode;
import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.stage.DesignTableStage;
import com.openjfx.database.app.stage.SQLGenStage;
import com.openjfx.database.app.utils.FXStringUtils;
import com.openjfx.database.common.VertexUtils;
import io.vertx.core.json.JsonObject;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;


import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * 数据库表节点
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTreeNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(
            20,
            20,
            "table_icon.png"
    );

    /**
     * 所属数据库
     */
    private final String database;

    private final JsonObject params = new JsonObject();

    public TableTreeNode(String database, String tableName, String uuid) {
        super(uuid, ICON_IMAGE);

        this.database = database;

        params.put(Constants.UUID, uuid);
        params.put(TABLE_NAME, database + "." + tableName);

        setValue(tableName);

        MenuItem sqlMenu = new MenuItem("生成SQL");
        MenuItem design = new MenuItem("设计表");
        MenuItem delete = new MenuItem("删除");

        sqlMenu.setOnAction(e -> new SQLGenStage(params));
        design.setOnAction(e -> new DesignTableStage(params));
        delete.setOnAction(e -> {
            var message = new JsonObject();
            message.put(ACTION, MainTabPane.EventBusAction.REMOVE);
            message.put(UUID, FXStringUtils.getTableTabUUID(uuid, database, tableName));
            VertexUtils.eventBus().send(MainTabPane.EVENT_BUS_ADDRESS, message);
            getParent().getChildren().remove(this);
        });
        addMenus(sqlMenu, design);
    }

    public String getDatabase() {
        return database;
    }

    @Override
    public void init() {
    }
}

package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.config.DbPreference;

import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.stage.CreateConnectionStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * Database child node
 *
 * @author yangkui
 * @since 1.0
 */
public class DBTreeNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(
            20,
            20,
            "mysql_icon.png"
    );

    public DBTreeNode(ConnectionParam param) {
        super(param, ICON_IMAGE);

        var editMenu = new MenuItem("编辑");
        var lostConnectMenu = new MenuItem("断开连接");
        var deleteMenu = new MenuItem("删除连接");
        var flush = new MenuItem("刷新");

        setValue(param.getName());

        flush.setOnAction((e) -> this.flush());

        editMenu.setOnAction(e -> new CreateConnectionStage(getUuid()));

        lostConnectMenu.setOnAction(e -> {
            DATABASE_SOURCE.close(getUuid());
            getChildren().clear();
            setLoading(false);
            removeAllTab();
        });

        deleteMenu.setOnAction(e -> {
            var r = DialogUtils.showAlertConfirm("确定要删除连接?");
            if (r) {
                //Delete disk cache
                DbPreference.deleteConnect(getUuid());
                //Delete memory cache
                DATABASE_SOURCE.close(getUuid());
                //Delete current node
                getParent().getChildren().remove(this);
                removeAllTab();
            }
        });
        addMenus(editMenu, lostConnectMenu, deleteMenu, flush);
        //listener param change
        paramProperty().addListener((observable, oldValue, newValue) -> flush());
    }

    @Override
    public void init() {
        if (getChildren().size() > 0 || isLoading()) {
            return;
        }
        setLoading(true);
        if (!getChildren().isEmpty()) {
            getChildren().clear();
        }
        //Start connecting to database
        var pool = DATABASE_SOURCE.createPool(param.get());
        var future = pool.getDql().showDatabase();
        future.onSuccess(sc ->
        {
            var schemeTreeNodes = sc.stream().map(s -> new SchemeTreeNode(s, param.get())).collect(Collectors.toList());
            Platform.runLater(() -> getChildren().addAll(schemeTreeNodes));
            setLoading(false);
            if (!isExpanded()) {
                Platform.runLater(() -> setExpanded(true));
            }
        });
        future.onFailure(t -> initFailed(t, "连接数据库失败"));
    }

    private void removeAllTab() {
        var message = new JsonObject();
        message.put(ACTION, MainTabPane.EventBusAction.REMOVE_MANY);
        message.put(FLAG, getUuid());
        //Move out the tabs related to the current database
        VertexUtils.send(MainTabPane.EVENT_BUS_ADDRESS, message);
    }
}

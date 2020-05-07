package com.openjfx.database.app.component.impl;

import com.openjfx.database.app.component.BaseTreeNode;
import com.openjfx.database.app.config.DbPreference;

import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.stage.CreateConnectionStage;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * 数据库子节点
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
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("确定要删除连接?");
            Optional<ButtonType> optional = alert.showAndWait();
            optional.ifPresent(t -> {
                if (t == ButtonType.OK) {
                    //删除磁盘缓存
                    DbPreference.deleteConnect(getUuid());
                    //删除内存缓存
                    DATABASE_SOURCE.close(getUuid());
                    //删除当前节点
                    getParent().getChildren().remove(this);
                    removeAllTab();
                }
            });
        });
        addMenus(editMenu, lostConnectMenu, deleteMenu, flush);
    }

    @Override
    public void init() {
        if (getChildren().size() > 0) {
            return;
        }
        setLoading(true);
        if (!getChildren().isEmpty()) {
            getChildren().clear();
        }
        //开始连接数据库
        var pool = DATABASE_SOURCE.createPool(param);
        var future = pool.getDql().showDatabase();
        future.onSuccess(sc ->
        {
            var schemeTreeNodes = sc.stream().map(s -> new SchemeTreeNode(s, param)).collect(Collectors.toList());
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
        //移出当前数据库相关的Tab
        VertexUtils.eventBus().send(MainTabPane.EVENT_BUS_ADDRESS, message);
    }
}

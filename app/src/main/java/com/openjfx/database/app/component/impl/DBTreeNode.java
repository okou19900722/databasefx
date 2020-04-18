package com.openjfx.database.app.component.impl;

import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.component.BaseTreeNode;
import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.stage.CreateConnectionStage;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.ACTION;
import static com.openjfx.database.app.config.Constants.UUID;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * 数据库子节点
 *
 * @author yangkui
 * @since 1.0
 */
public class DBTreeNode extends BaseTreeNode<String> {
    /**
     * 连接参数
     */
    private ConnectionParam param = null;

    private static final Image ICON_IMAGE = getLocalImage(
            20,
            20,
            "mysql_icon.png"
    );

    public DBTreeNode(String uuid) {
        super(uuid, ICON_IMAGE);
        initPreference();

        var editMenu = new MenuItem("编辑");
        var lostConnectMenu = new MenuItem("断开连接");
        var deleteMenu = new MenuItem("删除连接");
        var flush = new MenuItem("刷新");

        flush.setOnAction((e) -> this.flush());

        editMenu.setOnAction(e -> new CreateConnectionStage(uuid));

        lostConnectMenu.setOnAction(e -> {
            DATABASE_SOURCE.close(uuid);
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
                    DbPreference.deleteConnect(uuid);
                    //删除内存缓存
                    DATABASE_SOURCE.close(uuid);
                    //删除当前节点
                    getParent().getChildren().remove(this);
                    removeAllTab();
                }
            });
        });
        addMenus(editMenu, lostConnectMenu, deleteMenu, flush);
    }

    private void initPreference() {
        Optional<ConnectionParam> optional = DbPreference.getConnectionParam(uuid);
        optional.ifPresent(connectionParam -> {
            param = connectionParam;
            Platform.runLater(() -> setValue(param.getName()));
        });
    }

    @Override
    public void init() {
        if (getChildren().size() > 0) {
            return;
        }
        setLoading(true);
        initPreference();
        if (!getChildren().isEmpty()) {
            getChildren().clear();
        }
        //开始连接数据库
        AbstractDataBasePool pool = DATABASE_SOURCE.createPool(param);
        Future<List<String>> future = pool.getDql().showDatabase();
        future.onSuccess(sc ->
        {
            List<SchemeTreeNode> schemeTreeNodes = sc.stream().map(s -> new SchemeTreeNode(s, uuid)).collect(Collectors.toList());
            Platform.runLater(() -> getChildren().addAll(schemeTreeNodes));
            setLoading(false);
            if (!isExpanded()) {
                Platform.runLater(() -> setExpanded(true));
            }
        });
        future.onFailure(t -> initFailed(t, "连接数据库失败"));
    }

    private void removeAllTab() {
        JsonObject message = new JsonObject();
        message.put(ACTION, MainTabPane.EventBusAction.REMOVE_MANY);
        message.put(UUID, uuid);
        //移出当前数据库相关的Tab
        VertexUtils.eventBus().send(MainTabPane.EVENT_BUS_ADDRESS, message);
    }
}

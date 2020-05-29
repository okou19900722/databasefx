package com.openjfx.database.app.controls.impl;


import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.stage.DesignTableStage;
import com.openjfx.database.app.stage.SQLEditStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.ACTION;
import static com.openjfx.database.app.config.Constants.FLAG;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * scheme tree node
 *
 * @author yangkui
 * @since 1.0
 */
public class SchemeTreeNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(20, 20, "db_icon.png");

    private final String scheme;

    private final MenuItem flush = new MenuItem("刷新");

    private final MenuItem open = new MenuItem("打开数据库");

    private final MenuItem close = new MenuItem("关闭数据库");
    /**
     * event bus address
     */
    public final String eventBusAddress;

    public SchemeTreeNode(String scheme, ConnectionParam param) {
        super(param, ICON_IMAGE);
        this.eventBusAddress = getUuid() + "_" + scheme;

        this.scheme = scheme;

        setValue(scheme);


        final var deleteMenu = new MenuItem("删除数据库");
        final var sqlEditor = new MenuItem("SQL编辑器");
        final var createTable = new MenuItem("新建表");

        addMenuItem(open, createTable, sqlEditor, deleteMenu);

        flush.setOnAction(e -> flush());

        deleteMenu.setOnAction(event -> {
            var result = DialogUtils.showAlertConfirm("你确定要删除" + scheme + "?");
            if (result) {
                var dml = DATABASE_SOURCE.getDataBaseSource(getUuid()).getDdl();
                var future = dml.dropDatabase(scheme);
                future.onSuccess(r -> {
                    closeOpenTab();
                    //delete current node from parent node
                    getParent().getChildren().remove(this);
                    //remove cached database pool
                    DATABASE_SOURCE.close(getUuid());
                });
                future.onFailure(t -> DialogUtils.showErrorDialog(t, "删除schema失败"));
            }
        });

        //register event bus
        VertexUtils.eventBus().<JsonObject>consumer(eventBusAddress, msg -> {
            var body = msg.body();
            var action = body.getString(ACTION);
            if (EventBusAction.FLUSH_TABLE == EventBusAction.valueOf(action)) {
                flush();
            }
        });

        //show create table stage
        createTable.setOnAction(event -> {
            var params = new JsonObject();
            params.put(Constants.UUID, getUuid());
            params.put(Constants.SCHEME, scheme);
            params.put(Constants.TYPE, 0);
            new DesignTableStage(params);
        });

        //close scheme->close relative tab
        close.setOnAction(e -> {
            setExpanded(false);
            getChildren().clear();
            closeOpenTab();
            removeMenu(flush);
            removeMenu(close);
            addMenuItem(0, open);
        });

        //open database scheme
        open.setOnAction(event -> init());

        //open sql editor
        sqlEditor.setOnAction(e -> {
            var json = new JsonObject();
            json.put(Constants.UUID, param.getUuid());
            json.put(Constants.SCHEME, getValue());
            new SQLEditStage(json);
        });
    }

    /**
     * by event bus notify {@link MainTabPane} close current scheme relative tab
     */
    private void closeOpenTab() {
        var message = new JsonObject();
        message.put(ACTION, MainTabPane.EventBusAction.REMOVE_MANY);
        message.put(FLAG, getUuid() + "_" + scheme);
        VertexUtils.send(MainTabPane.EVENT_BUS_ADDRESS, message);
    }

    @Override
    public void init() {
        if (getChildren().size() > 0 || isLoading()) {
            return;
        }
        setLoading(true);
        var dcl = DATABASE_SOURCE.getDataBaseSource(getUuid()).getDql();
        var future = dcl.showTables(getValue());
        future.onSuccess(tables ->
        {
            var tas = tables.stream().map(s -> new TableTreeNode(getValue(), s, param.get())).collect(Collectors.toList());
            Platform.runLater(() -> {
                getChildren().addAll(tas);
                if (tas.size() > 0) {
                    setExpanded(true);
                }
                addMenuItem(0, close);
                addMenuItem(flush);
                removeMenu(open);
            });
            setLoading(false);
        });
        future.onFailure(t -> initFailed(t, "获取scheme失败"));
    }

    /**
     * Event bus address
     *
     * @author yangkui
     * @since 1.0
     */
    public enum EventBusAction {
        FLUSH_TABLE
    }
}

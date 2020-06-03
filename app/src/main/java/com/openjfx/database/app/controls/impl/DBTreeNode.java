package com.openjfx.database.app.controls.impl;

import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.config.DbPreference;

import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.stage.CreateConnectionStage;
import com.openjfx.database.app.stage.CreateSchemeStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;
import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * Database child node
 *
 * @author yangkui
 * @since 1.0
 */
public class DBTreeNode extends BaseTreeNode<String> {

    private final MenuItem loseConnect;


    private final MenuItem openConnect;

    private static final Image ICON_IMAGE = getLocalImage(
            20,
            20,
            "mysql_icon.png"
    );

    public DBTreeNode(ConnectionParam param) {
        super(param, ICON_IMAGE);

        loseConnect = new MenuItem(I18N.getString("menu.databasefx.tree.lose.connection"));

        openConnect = new MenuItem(I18N.getString("menu.databasefx.tree.open.connection"));


        var editMenu = new MenuItem(I18N.getString("menu.databasefx.tree.edit"));

        var deleteMenu = new MenuItem(I18N.getString("menu.databasefx.tree.delete"));

        setValue(param.getName());


        editMenu.setOnAction(e -> new CreateConnectionStage(getUuid()));

        loseConnect.setOnAction(e -> {
            DATABASE_SOURCE.close(getUuid());
            getChildren().clear();
            setLoading(false);
            removeAllTab();
            //dynamic remove MenuItem
            removeMenu(loseConnect);
            addMenuItem(0, openConnect);
        });

        deleteMenu.setOnAction(e -> {
            var r = DialogUtils.showAlertConfirm(I18N.getString("menu.databasefx.tree.delete.tips"));
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
        openConnect.setOnAction(e -> init());
        addMenuItem(openConnect, editMenu, deleteMenu);
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
        //test connection
        var future = pool.getDql().heartBeatQuery();
        future.onSuccess(sc ->
        {
            Platform.runLater(() -> {
                //dynamic add MenuItem
                addMenuItem(0, loseConnect);
                removeMenu(openConnect);
                if (!isExpanded()) {
                    setExpanded(true);
                }
                var database = new SchemeFolderNode(getParam());
                var user = new UserFolderNode(getParam());
                getChildren().addAll(database, user);
            });
            setLoading(false);
        });
        future.onFailure(t -> initFailed(t, I18N.getString("menu.databasefx.tree.open.tips")));
    }

    private void removeAllTab() {
        var message = new JsonObject();
        message.put(ACTION, MainTabPane.EventBusAction.REMOVE_MANY);
        message.put(FLAG, getUuid());
        //Move out the tabs related to the current database
        VertexUtils.send(MainTabPane.EVENT_BUS_ADDRESS, message);
    }
}

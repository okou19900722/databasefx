package com.openjfx.database.app.component.impl;


import com.openjfx.database.DDL;
import com.openjfx.database.DQL;
import com.openjfx.database.app.component.BaseTreeNode;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.Future;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * scheme tree node
 *
 * @author yangkui
 * @since 1.0
 */
public class SchemeTreeNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(20, 20, "db_icon.png");

    public SchemeTreeNode(String scheme, ConnectionParam param) {
        super(param, ICON_IMAGE);

        setValue(scheme);

        MenuItem flush = new MenuItem("刷新");
        MenuItem deleteMenu = new MenuItem("删除");

        addMenus(flush, deleteMenu);

        flush.setOnAction(e -> flush());

        deleteMenu.setOnAction(event -> {
            //删除scheme
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("你确定要删除" + scheme + "?");
            Optional<ButtonType> optional = alert.showAndWait();
            optional.ifPresent(buttonType -> {
                //确定删除
                if (buttonType == ButtonType.OK) {
                    DDL dml = DATABASE_SOURCE.getDataBaseSource(getUuid()).getDdl();
                    Future<Void> future = dml.dropDatabase(scheme);
                    future.onSuccess(r -> {
                        //删除当前节点
                        getParent().getChildren().remove(this);
                        //移出缓存数据
                        DATABASE_SOURCE.close(getUuid());
                    });
                    future.onFailure(t -> DialogUtils.showErrorDialog(t, "删除schema失败"));
                }
            });
        });
    }

    @Override
    public void init() {
        if (getChildren().size() > 0) {
            return;
        }
        setLoading(true);
        DQL dcl = DATABASE_SOURCE.getDataBaseSource(getUuid()).getDql();
        Future<List<String>> future = dcl.showTables(getValue());
        future.onSuccess(tables ->
        {
            var tas = tables.stream().map(s -> new TableTreeNode(getValue(), s, param)).collect(Collectors.toList());
            Platform.runLater(() -> {
                getChildren().addAll(tas);
                if (tas.size() > 0) {
                    setExpanded(true);
                }
            });
            setLoading(false);
        });
        future.onFailure(t -> initFailed(t, "获取scheme失败"));
    }
}

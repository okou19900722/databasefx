package com.openjfx.database.app.component.impl;


import com.openjfx.database.DDL;
import com.openjfx.database.DQL;
import com.openjfx.database.app.component.BaseTreeNode;
import com.openjfx.database.app.utils.DialogUtils;
import io.vertx.core.Future;
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
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * scheme tree node
 *
 * @author yangkui
 * @since 1.0
 */
public class SchemeTreeNode extends BaseTreeNode<String> {

    private static final Image ICON_IMAGE = getLocalImage(20, 20, "db_icon.png");

    private MenuItem flush = new MenuItem("刷新");

    private MenuItem deleteMenu = new MenuItem("删除");

    public SchemeTreeNode(String scheme, String uuid) {
        super(uuid);
        ImageView imageView = new ImageView(ICON_IMAGE);

        setGraphic(imageView);

        setValue(scheme);

        addMenus(flush, deleteMenu);

        flush.setOnAction(e ->this.flush());

        deleteMenu.setOnAction(event -> {
            //删除scheme
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("你确定要删除"+scheme+"?");
            Optional<ButtonType> optional = alert.showAndWait();
            optional.ifPresent(buttonType -> {
                //确定删除
                if (buttonType==ButtonType.OK){
                    DDL dml = DATABASE_SOURCE.getDataBaseSource(uuid).getDdl();
                    Future<Void> future = dml.dropDatabase(scheme);
                    future.onSuccess(r->{
                        //删除当前节点
                        getParent().getChildren().remove(this);
                        //移出缓存数据
                        DATABASE_SOURCE.close(uuid);
                    });
                    future.onFailure(t->DialogUtils.showErrorDialog(t,"删除schema失败"));
                }
            });
        });
    }

    @Override
    public void init() {
        if (loading) {
            return;
        }

        if (getChildren().size() > 0) {
            getChildren().clear();
        }

        DQL dcl = DATABASE_SOURCE.getDataBaseSource(uuid).getDql();
        Future<List<String>> future = dcl.showTables(getValue());
        future.onSuccess(tables ->
        {
            List<TableTreeNode> tas = tables.stream().map(s -> new TableTreeNode(getValue(), s, uuid))
                    .collect(Collectors.toList());
            Platform.runLater(() -> getChildren().addAll(tas));
            loading = true;
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "获取scheme失败"));
    }

}

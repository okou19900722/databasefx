package com.openjfx.database.app.controls.impl;


import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.stage.SQLEditStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.app.utils.EventBusUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;


import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;
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

    private final MenuItem close;

    public SchemeTreeNode(String scheme, ConnectionParam param) {
        super(param, ICON_IMAGE);
        this.scheme = scheme;
        setValue(scheme);

        var tableFolder = new TableFolderNode(getParam(), scheme);
        var viewFolder = new ViewFolderNode(getParam(), scheme);


        getChildren().addAll(tableFolder, viewFolder);

        close = new MenuItem(I18N.getString("menu.databasefx.tree.close.database"));

        final var deleteMenu = new MenuItem(I18N.getString("menu.databasefx.tree.delete.database"));
        final var sqlEditor = new MenuItem(I18N.getString("men.databasefx.tree.sql.editor"));

        addMenuItem(sqlEditor, deleteMenu);


        deleteMenu.setOnAction(event -> {
            var result = DialogUtils.showAlertConfirm(I18N.getString("menu.databasefx.tree.delete.database.tips") + " " + scheme + "?");
            if (result) {
                var dml = DATABASE_SOURCE.getDataBaseSource(getUuid()).getDdl();
                var future = dml.dropDatabase(scheme);
                future.onSuccess(r -> {
                    //delete current node from parent node
                    getParent().getChildren().remove(this);
                    EventBusUtils.closeSchemeRelationTab(getUuid(), scheme);
                });
                future.onFailure(t -> DialogUtils.showErrorDialog(t, I18N.getString("menu.databasefx.tree.delete.database.fail.tips")));
            }
        });

        //close scheme->close relative tab
        close.setOnAction(e -> {
            setExpanded(false);
            getChildren().clear();
            removeMenu(close);
            EventBusUtils.closeSchemeRelationTab(getUuid(), scheme);
        });

        //open sql editor
        sqlEditor.setOnAction(e -> {
            var json = new JsonObject();
            json.put(Constants.UUID, param.getUuid());
            json.put(Constants.SCHEME, getValue());
            new SQLEditStage(json);
        });
    }

    @Override
    public void init() {
    }
}

package com.openjfx.database.app.controller;

import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.utils.TableColumnUtils;
import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.app.utils.RobotUtils;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.utils.StringUtils;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableView;

import java.util.*;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

/**
 * sql editor controller
 *
 * @author yangkui
 * @since 1.0
 */
public class SQLEditController extends BaseController<JsonObject> {
    @FXML
    private TableView<ObservableList<StringProperty>> tableView;

    @FXML
    private SQLEditor sqlEditor;

    private AbstractDataBasePool client;

    @Override
    public void init() {
        var uid = data.getString(Constants.UUID);

        var optional = DbPreference.getConnectionParam(uid);

        var uuid = "temp_" + uid;

        var scheme = data.getString(Constants.SCHEME);

        if (optional.isPresent()) {
            if (StringUtils.nonEmpty(scheme)) {
                client = DATABASE_SOURCE.createPool(optional.get(), uuid, scheme, 1);
            } else {
                client = DATABASE_SOURCE.createPool(optional.get(), uuid, 1);
            }
        } else {
            DialogUtils.showAlertInfo(resourceBundle.getString("controller.sql.editor.disable"));
            stage.close();
            return;
        }
        //加载scheme
        var param = client.getConnectionParam();
        final String title;
        if (StringUtils.nonEmpty(scheme)) {
            title = param.getName() + "<" + param.getHost() + "/" + scheme + ">";
        } else {
            title = param.getName() + "<" + param.getHost() + ">";
        }
        stage.setTitle(title);

        var future = client.getConnection();
        future.onComplete(ar -> {
            if (ar.failed()) {
                DialogUtils.showErrorDialog(ar.cause(), resourceBundle.getString("controller.sql.editor.disable"));
                return;
            }
            var con = ar.result();
            //place into database source pool
            con.close();
        });

        stage.setOnCloseRequest(event -> DATABASE_SOURCE.close(uuid));
    }


    @FXML
    public void executeSql() {
        var sql = sqlEditor.getText();
        if (StringUtils.isEmpty(sql)) {
            DialogUtils.showNotification(resourceBundle.getString("controller.sql.editor.sql.empty"), Pos.TOP_CENTER, NotificationType.WARNING);
            return;
        }
        var fut = client.getPool().query(sql);
        fut.onSuccess(rs -> {
            var columnNames = rs.columnsNames();
            var convert = client.getDataConvert();
            final List<String[]> values;
            if (columnNames == null || columnNames.isEmpty()) {
                var columnName = "affected rows";
                columnNames = Collections.singletonList(columnName);
                values = Collections.singletonList(new String[]{String.valueOf(rs.rowCount())});
            } else {
                values = convert.toConvert(rs);
            }
            createData(columnNames, values);
            DialogUtils.showNotification("execute sql success!", Pos.TOP_CENTER, NotificationType.INFORMATION);
        });
        fut.onFailure(t -> DialogUtils.showErrorDialog(t, resourceBundle.getString("controller.sql.editor.sql.executor.fail")));
    }

    @FXML
    public void copySql(ActionEvent event) {
        var sql = sqlEditor.getText();
        if (StringUtils.isEmpty(sql)) {
            DialogUtils.showNotification(resourceBundle.getString("controller.sql.editor.sql.empty"), Pos.TOP_CENTER, NotificationType.INFORMATION);
            return;
        }
        RobotUtils.addStrClipboard(sql);
    }

    @FXML
    public void clearSql(ActionEvent event) {
        sqlEditor.deleteText(0, sqlEditor.getText().length());
    }


    private void createData(List<String> fields, List<String[]> data) {
        Platform.runLater(() -> tableView.getColumns().clear());
        var columns = TableColumnUtils.createTableDataColumnWithField(fields);
        var list = FXCollections.<ObservableList<StringProperty>>observableArrayList();
        for (var row : data) {
            var item = FXCollections.<StringProperty>observableArrayList();
            for (var val : row) {
                item.add(new SimpleStringProperty(val));
            }
            list.add(item);
        }
        Platform.runLater(() -> {
            tableView.getColumns().addAll(columns);
            tableView.getItems().clear();
            tableView.getItems().addAll(list);
            tableView.refresh();
        });
    }
}

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
 * sql编辑器控制器
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

        optional.ifPresent(param -> client = DATABASE_SOURCE.createPool(param, uuid, scheme, 1));
        //加载scheme
        var param = client.getConnectionParam();
        var title = param.getName() + "<" + param.getHost() + "/" + scheme + ">";
        var future = client.getConnection();

        future.onFailure(t -> DialogUtils.showErrorDialog(t, "获取连接失败"));

        stage.setTitle(title);

        stage.setOnCloseRequest(event -> DATABASE_SOURCE.close(uuid));
    }


    @FXML
    public void executeSql() {
        var sql = sqlEditor.getText();
        if (StringUtils.isEmpty(sql)) {
            DialogUtils.showNotification("sql语句不能为空", Pos.TOP_CENTER, NotificationType.WARNING);
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
        fut.onFailure(t -> DialogUtils.showErrorDialog(t, "execute sql failed"));
    }

    @FXML
    public void copySql(ActionEvent event) {
        var sql = sqlEditor.getText();
        if (StringUtils.isEmpty(sql)) {
            DialogUtils.showNotification("sql不能为空", Pos.TOP_CENTER, NotificationType.INFORMATION);
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

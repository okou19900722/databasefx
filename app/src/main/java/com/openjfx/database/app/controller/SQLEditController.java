package com.openjfx.database.app.controller;

import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.controls.TableDataColumn;
import com.openjfx.database.app.utils.TableCellUtils;
import com.openjfx.database.app.utils.TableColumnUtils;
import com.openjfx.database.mysql.JSqlParserHelper;
import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.controls.TableDataCell;
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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.sf.jsqlparser.JSQLParserException;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String scheme;

    private AbstractDataBasePool client;

    @Override
    public void init() {
        var uuid = data.getString(Constants.UUID);

        scheme = data.getString(Constants.SCHEME);

        //加载scheme
        client = DATABASE_SOURCE.getDataBaseSource(uuid);
        var param = client.getConnectionParam();
        var title = param.getName() + "<" + param.getHost() + "/" + scheme + ">";

        stage.setTitle(title);

        stage.setOnCloseRequest(event -> sqlEditor.dispose());
    }


    @FXML
    public void executeSql(ActionEvent event) {
        var str = sqlEditor.getText();
        if (StringUtils.isEmpty(str)) {
            DialogUtils.showNotification("sql语句不能为空", Pos.TOP_CENTER, NotificationType.WARNING);
            return;
        }
        String sql;
        try {
            sql = JSqlParserHelper.transform(str, scheme);
        } catch (JSQLParserException e) {
            DialogUtils.showErrorDialog(e, "sql转换异常");
            return;
        }
        var b = sql.toLowerCase().trim();
        var a = b.startsWith("select") | b.startsWith("show");
        if (a) {
            executeSqlQuery(sql);
        } else {
            executeSqlUpdate(sql);
        }
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

    public void executeSqlQuery(String sql) {
        Platform.runLater(() -> tableView.getColumns().clear());
        var future = client.getDql().executeSql(sql);
        future.onSuccess(rs -> {
            for (Map.Entry<List<String>, List<String[]>> entry : rs.entrySet()) {
                createData(entry.getKey(), entry.getValue());
            }
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "执行查询失败"));
    }

    public void executeSqlUpdate(String sql) {
        var future = client.getDml().executeSqlUpdate(sql);
        future.onSuccess(rs -> {
            //创建列
            var columnName = "affected rows";
            var fields = Collections.singletonList(columnName);
            var values = Collections.singletonList(new String[]{rs.toString()});
            createData(fields, values);

        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "更新失败"));
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

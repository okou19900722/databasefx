package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.component.DesignOptionBox;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.controls.DesignTableView;
import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.model.AbstractDesignTableChangeModel;
import com.openjfx.database.app.model.DesignTableModel;
import com.openjfx.database.app.model.impl.RegularFieldTableChangeModel;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

/**
 * 设计表控制器
 *
 * @author yangkui
 * @since 1.0
 */
public class DesignTableController extends BaseController<JsonObject> {

    @FXML
    private TabPane tabPane;

    @FXML
    private HBox topBox;

    @FXML
    private DesignTableView<DesignTableModel> fieldTable;

    @FXML
    private SplitPane splitPane;

    @FXML
    private DesignOptionBox box;

    @FXML
    private SQLEditor sqlEditor;

    private AbstractDataBasePool pool;

    private final List<Button> actionList = new ArrayList<>();

    private final RegularFieldTableChangeModel regularFieldTableChangeModel = new RegularFieldTableChangeModel();

    private final List<TableColumnMeta> columnMetas = new ArrayList<>();

    @Override
    public void init() {
        intiTable(fieldTable, DesignTableModel.class);
        initDataTable();
        for (Tab tab : tabPane.getTabs()) {
            tab.setClosable(false);
        }
        var i = 0;
        for (var child : topBox.getChildren()) {
            var button = (Button) child;
            if (i != 0) {
                actionList.add(button);
            }
            button.setOnAction(e -> listAction(button.getUserData().toString()));
            i++;
        }
        tabSelectChange(0);
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            tabSelectChange(index);
            if (index == 6) {
                var sql = regularFieldTableChangeModel.getUpdateSql(getTableName(false), columnMetas);
                sqlEditor.setText(sql);
            }
        });

        //listener fieldTable select change
        fieldTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            if (index == -1) {
                return;
            }
            var item = fieldTable.getItems().get(index);
            box.updateValue(item);
            //listener field every value change
            item.setCallback((old, value, fieldName) -> regularFieldTableChangeModel.addChange(RowChangeModel.ChangeType.UPDATE, index, fieldName, old, value));
        });

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            var t = 800;
            var position = 0.7;
            if (newValue.doubleValue() > t) {
                position = 0.8;
            }
            splitPane.setDividerPosition(0, position);
        });

        //dynamic show/hide bottom DesignOptionBox
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            if (index == -1) {
                return;
            }
            var items = splitPane.getItems();
            if (index != 0 && items.size() > 1) {
                items.remove(1);
            }
            if (index == 0 && items.size() == 1) {
                items.add(box);
            }
        });
    }

    private <T> void intiTable(DesignTableView<T> tableView, Class<T> t) {
        var fields = t.getDeclaredFields();
        var tableColumns = tableView.getColumns();
        for (int i = 0; i < tableColumns.size(); i++) {
            var field = fields[i];
            var column = tableColumns.get(i);
            column.setCellValueFactory(new PropertyValueFactory<>(field.getName()));
        }
    }

    private void tabSelectChange(int index) {
        var cc = topBox.getChildren();
        var length = topBox.getChildren().size();
        cc.remove(1, length);
        for (Button node : actionList) {
            var useData = node.getUserData().toString();
            var indexNest = useData.split(",");
            for (var i : indexNest) {
                var ab = i.split("_");
                var a = Integer.parseInt(ab[0]);
                var b = Integer.parseInt(ab[1]);
                if (a == index) {
                    var size = cc.size();
                    if (b >= size - 1) {
                        cc.add(node);
                    } else {
                        cc.add(b, node);
                    }
                }
            }
        }
    }

    private void initDataTable() {
        var uuid = data.getString(Constants.UUID);
        var scheme = data.getString(Constants.SCHEME);
        var table = data.getString(Constants.TABLE_NAME, "");
        var title = "无标题" + " @ " + scheme;
        if (!"".equals(table)) {
            title = table + " @ " + scheme;
            var tableName = scheme + "." + table;
            pool = DATABASE_SOURCE.getDataBaseSource(uuid);
            var future = pool.getDql().showColumns(tableName);
            future.onSuccess(rs -> {
                columnMetas.addAll(rs);
                var list = DesignTableModel.build(rs);
                Platform.runLater(() -> {
                    fieldTable.setItems(FXCollections.observableList(list));
                    if (list.size() > 0) {
                        //default select first row
                        fieldTable.getSelectionModel().select(0);
                    }
                });
            });
            future.onFailure(Throwable::printStackTrace);
        }
        stage.setTitle(title);
    }

    private void listAction(final String ij) {
        //save
        if ("0_0".equals(ij)) {
            var title = stage.getTitle();
            var array = title.split("@");
            var temp = array[0].trim();
            var tableName = "";
            var sqlType = 0;
            //Prompt user for table name
            if ("无标题".equals(temp)) {
                var table = DialogUtils.showInputDialog("请输入表名");
                if (StringUtils.isEmpty(table)) {
                    DialogUtils.showAlertInfo("表明不能为空");
                    return;
                }
                tableName = array[1].trim() + "." + table;
                sqlType = 1;
            } else {
                tableName = array[1].trim() + "." + array[0].trim();
            }
        }
        //new create  row
        if ("0_1".equals(ij)) {
            var model = new DesignTableModel();
            var items = fieldTable.getItems();
            items.add(model);
            var index = items.size() - 1;
            fieldTable.getSelectionModel().select(index);
            //add row
            regularFieldTableChangeModel.addChange(RowChangeModel.ChangeType.CREATE, index, "", "", "");
        }
        //delete row
        if ("0_3".equals(ij)) {
            var index = fieldTable.getSelectionModel().getSelectedIndex();
            if (index != -1) {
                fieldTable.getItems().remove(index);
                regularFieldTableChangeModel.addChange(RowChangeModel.ChangeType.DELETE, index, "", "", "");
            }
        }
    }

    private String getTableName(boolean isInput) {
        var title = stage.getTitle();
        var array = title.split("@");
        var temp = array[0].trim();
        var tableName = "";
        //Prompt user for table name
        if ("无标题".equals(temp)) {
            if (isInput) {
                var table = DialogUtils.showInputDialog("请输入表名");
                if (StringUtils.isEmpty(table)) {
                    DialogUtils.showAlertInfo("表名不能为空");
                    return "";
                }
                tableName = array[1].trim() + "." + table;
            } else {
                tableName = array[1].trim() + "." + "Untitled";
            }
        } else {
            tableName = array[1].trim() + "." + array[0].trim();
        }

        return tableName;
    }
}

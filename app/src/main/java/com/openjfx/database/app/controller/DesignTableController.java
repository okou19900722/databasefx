package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.component.DesignOptionBox;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.controls.DesignTableView;
import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.model.DesignTableModel;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.enums.DesignTableOperationSource;
import com.openjfx.database.enums.DesignTableOperationType;
import com.openjfx.database.model.RowChangeModel;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import com.openjfx.database.app.model.TableFieldChangeModel;

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

    @FXML
    private TextArea commentTextArea;

    private AbstractDataBasePool pool;

    private final List<Button> actionList = new ArrayList<>();

    private final TableFieldChangeModel tableFieldChangeModel = new TableFieldChangeModel();

    private final List<TableColumnMeta> columnMetas = new ArrayList<>();
    /**
     * design table type
     * 0 create table
     * 1 update table
     */
    private int type = 0;

    @Override
    public void init() {
        this.type = data.getInteger(Constants.TYPE, 0);
        intiTable(fieldTable, DesignTableModel.class);
        initDataTable();
        for (Tab tab : tabPane.getTabs()) {
            tab.setClosable(false);
        }
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            var tab = tabPane.getTabs().get(index);
            if (index == 2) {
                var sql = getSql(getTableName(false));
                sqlEditor.setText(sql);
            }
            if (index == 1) {
                var ua = tab.getUserData();
                if (ua == null) {
                    commentTextArea.textProperty().addListener((observable1, oldValue1, newValue1) -> tableFieldChangeModel.tableCommentChange(oldValue1, newValue1));
                    tab.setUserData("COMMENT");
                }
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
            item.setCallback((meta, value, fieldName) -> tableFieldChangeModel.fieldChange(meta, DesignTableOperationType.UPDATE, index, fieldName, value));
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

    private void initDataTable() {
        var uuid = data.getString(Constants.UUID);
        pool = DATABASE_SOURCE.getDataBaseSource(uuid);
        var scheme = data.getString(Constants.SCHEME);
        var table = data.getString(Constants.TABLE_NAME);
        updateTableName(table);
        if (type == 1) {
            final var tableName = scheme + "." + table;
            final var future = pool.getDql().showColumns(tableName);
            //clear design table
            Platform.runLater(() -> fieldTable.getItems().clear());
            //load design table info
            var fut = future.compose(rs -> {
                columnMetas.clear();
                columnMetas.addAll(rs);
                var list = DesignTableModel.build(rs);
                Platform.runLater(() -> {
                    fieldTable.setItems(FXCollections.observableList(list));
                    if (list.size() > 0) {
                        //default select first row
                        fieldTable.getSelectionModel().select(0);
                    }
                });
                return pool.getDql().getCreateTableComment(tableName);
            });
            fut.onSuccess(comment -> Platform.runLater(() -> commentTextArea.setText(comment)));
            fut.onFailure(t -> DialogUtils.showErrorDialog(t, "设计表初始化失败"));
        }
    }

    @FXML
    public void save(ActionEvent event) {
        var tableName = getTableName(true);
        if (StringUtils.isEmpty(tableName)) {
            return;
        }
        var sql = getSql(tableName);
        if (StringUtils.isEmpty(sql)) {
            return;
        }
        var future = pool.getPool().query(sql);
        future.onSuccess(ar -> {
            if (type == 0) {
                this.type = 1;
                var temp = tableName.split("\\.")[1];
                data.put(Constants.TABLE_NAME, temp);
            }
            tableFieldChangeModel.clear();
            //refresh data table
            initDataTable();
            DialogUtils.showNotification("更新成功", Pos.TOP_CENTER, NotificationType.INFORMATION);
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "更新/创建表失败"));
    }

    @FXML
    public void createNewField(ActionEvent event) {
        var model = new DesignTableModel();
        var items = fieldTable.getItems();
        items.add(model);
        var index = items.size() - 1;
        fieldTable.getSelectionModel().select(index);
        //add row
        tableFieldChangeModel.fieldChange(null, DesignTableOperationType.CREATE, index, null, "");
    }

    @FXML
    public void deleteField(ActionEvent event) {
        var index = fieldTable.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            //remove item from table
            var item = fieldTable.getItems().remove(index);
            tableFieldChangeModel.deleteChange(item.getTableColumnMeta(), DesignTableOperationSource.TABLE_FIELD, index);
        }
    }

    @FXML
    public void setPrimaryKey(ActionEvent event) {
        var index = fieldTable.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            return;
        }
        var item = fieldTable.getItems().get(index);
        //select key
        item.getKey().setSelected(true);
    }

    private String getSql(final String tableName) {
        var sql = "";
        if (type == 1) {
            sql = tableFieldChangeModel.getUpdateSql(tableName, columnMetas);
        } else {
            sql = tableFieldChangeModel.getCreateSql(tableName);
        }
        return sql;
    }

    private String getTableName(boolean isInput) {
        var title = stage.getTitle();
        var array = title.split("@");
        var temp = array[0].trim();
        var tableName = "";
        //Prompt user for table name
        if ("Untitled".equals(temp)) {
            if (isInput) {
                var table = DialogUtils.showInputDialog("请输入表名");
                if (StringUtils.isEmpty(table)) {
                    DialogUtils.showAlertInfo("表名不能为空");
                    return "";
                }
                tableName = array[1].trim() + "." + table;
            } else {
                tableName = array[1].trim() + "." + temp;
            }
        } else {
            tableName = array[1].trim() + "." + array[0].trim();
        }

        return tableName;
    }

    private void updateTableName(final String table) {
        var scheme = data.getString(Constants.SCHEME);
        final String title;
        if (StringUtils.isEmpty(table)) {
            title = "Untitled" + " @ " + scheme;
        } else {
            title = table + " @ " + scheme;
        }
        Platform.runLater(() -> stage.setTitle(title));
    }
}

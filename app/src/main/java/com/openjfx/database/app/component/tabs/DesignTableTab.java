package com.openjfx.database.app.component.tabs;


import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.component.DesignOptionBox;
import com.openjfx.database.app.controls.DesignTableView;
import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.model.DesignTableModel;
import com.openjfx.database.app.model.TableFieldChangeModel;
import com.openjfx.database.app.model.tab.meta.DesignTabModel;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.app.utils.EventBusUtils;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.enums.DesignTableOperationSource;
import com.openjfx.database.enums.DesignTableOperationType;
import com.openjfx.database.model.TableColumnMeta;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;


/**
 * Design table tab
 *
 * @author yangkui
 * @since 1.0
 */
public class DesignTableTab extends BaseTab<DesignTabModel> {
    @FXML
    private TabPane tabPane;
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

    private final TableFieldChangeModel tableFieldChangeModel = new TableFieldChangeModel();

    private final List<TableColumnMeta> columnMetas = new ArrayList<>();
    /**
     * un-title
     */
    private final String UN_TITLE = "Untitled";

    private final static Image IMAGE_ICON = AssetUtils.getLocalImage(20, 20, "design_table_icon.png");

    public DesignTableTab(DesignTabModel model) {
        super(model);
        loadView("design_tab_view.fxml");
        setTabIcon(IMAGE_ICON);
    }

    @Override
    public void init() {
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
        updateTableName();
        pool = DATABASE_SOURCE.getDataBaseSource(model.getUuid());
        if (model.getDesignTableType() == DesignTabModel.DesignTableType.UPDATE) {
            var tableName = model.getScheme() + "." + model.getTableName();
            var future = pool.getDql().showColumns(tableName);
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
            fut.onFailure(t -> DialogUtils.showErrorDialog(t, i18nStr("controller.design.table.init.fail")));
        }
    }

    @FXML
    public void save() {
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
            if (model.getDesignTableType() == DesignTabModel.DesignTableType.CREATE) {
                model.setDesignTableType(DesignTabModel.DesignTableType.UPDATE);
                model.setTableName(tableName);
                EventBusUtils.tableFolderFlushList(model.getUuid(), model.getScheme());
            }
            tableFieldChangeModel.clear();
            //refresh data table
            initDataTable();
            DialogUtils.showNotification(i18nStr("controller.design.table.update.success"), Pos.TOP_CENTER, NotificationType.INFORMATION);
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, i18nStr("controller.design.table.update.fail")));
    }

    @FXML
    public void createNewField() {
        var model = new DesignTableModel();
        var items = fieldTable.getItems();
        items.add(model);
        var index = items.size() - 1;
        //note this row code must place first row
        tableFieldChangeModel.fieldChange(null, DesignTableOperationType.CREATE, index, null, "");
        //init property
        fieldTable.getSelectionModel().select(index);
        model.getFieldLength().setText("0");
        model.getFieldPoint().setText("0");
    }

    @FXML
    public void deleteField() {
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

    private String getSql(String tableName) {
        final String sql;
        tableName = model.getScheme() + "." + tableName;
        if (model.getDesignTableType() == DesignTabModel.DesignTableType.UPDATE) {
            sql = tableFieldChangeModel.getUpdateSql(tableName, columnMetas);
        } else {
            sql = tableFieldChangeModel.getCreateSql(tableName);
        }
        return sql;
    }

    private String getTableName(boolean input) {
        var tableName = UN_TITLE;
        //Prompt user for table name
        if (StringUtils.isEmpty(model.getTableName()) && input) {
            tableName = DialogUtils.showInputDialog(i18nStr("controller.design.table.input"));
        } else {
            if (StringUtils.nonEmpty(model.getTableName())) {
                tableName = model.getTableName();
            }
        }
        return tableName;
    }

    private void updateTableName() {
        final String title;
        final String table = model.getTableName();
        if (StringUtils.isEmpty(table)) {
            title = UN_TITLE + "@" + model.getScheme();
        } else {
            title = table + "@" + model.getScheme();
        }

        Platform.runLater(() -> setText(title));
    }
}

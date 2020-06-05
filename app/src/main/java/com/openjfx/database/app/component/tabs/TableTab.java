package com.openjfx.database.app.component.tabs;

import com.openjfx.database.DML;
import com.openjfx.database.app.TableDataHelper;
import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.component.SearchPopup;
import com.openjfx.database.app.controls.TableDataView;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.model.TableSearchResultModel;
import com.openjfx.database.app.model.tab.meta.TableTabModel;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.app.utils.TableColumnUtils;
import com.openjfx.database.app.utils.TableDataUtils;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import com.openjfx.database.mysql.MysqlHelper;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.*;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.DatabaseFX.I18N;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;
import static com.openjfx.database.common.config.StringConstants.NULL;


/**
 * table tab
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTab extends BaseTab<TableTabModel> {
    /**
     * Data table related control icons
     */
    private static final Image FLAG_IMAGE = getLocalImage(20, 20, "point.png");
    /**
     * Set table icon dynamically for current table type
     */
    private static final Image TABLE_VIEW_ICON = getLocalImage(20, 20, "table_view_icon.png");
    private static final Image TABLE_ICON = getLocalImage(20, 20, "table_icon.png");

    @FXML
    private BorderPane borderPane;

    @FXML
    private TableDataView tableView;
    @FXML
    private Button addData;
    @FXML
    private Button flush;
    @FXML
    private Button next;
    @FXML
    private Button last;
    @FXML
    private Button submit;
    @FXML
    private Button delete;
    @FXML
    private TextField numberTextField;
    @FXML
    private Label totalLabel;
    @FXML
    private Label indexCounter;
    @FXML
    private Label pageCounter;

    private final AbstractDataBasePool pool;

    private final Label flag = new Label();
    private final List<TableColumnMeta> metas = new ArrayList<>();
    private final List<TableSearchResultModel> searchList = new ArrayList<>();
    private final SearchPopup searchPopup = SearchPopup.complexPopup();

    /**
     * Determine whether the primary key exists in the current table.
     * If it does not exist, it is not allowed to update.
     * Because there is no primary key, it is likely to cause data update failure.
     */
    private TableColumnMeta primaryKeyMeta = null;

    private int pageIndex = 1;
    private int pageSize = 100;

    public TableTab(TableTabModel model) {
        super(model);
        if (model.getTableType() == TableTabModel.TableType.BASE_TABLE) {
            setTabIcon(TABLE_ICON);
        } else {
            setTabIcon(TABLE_VIEW_ICON);
        }
        pool = DATABASE_SOURCE.getDataBaseSource(model.getUuid());
        var title = model.getTableName() + "@" + model.getDatabase() + "(" + model.getServerName() + ")";
        setText(title);
        //dynamic obtain table comment
        var future = pool.getDql().getCreateTableComment(model.getTable());
        future.onComplete(ar -> {
            final String tooltip;
            if (ar.succeeded() && StringUtils.nonEmpty(ar.result())) {
                tooltip = ar.result();
            } else {
                tooltip = title;
            }
            Platform.runLater(() -> setTooltip(new Tooltip(tooltip)));
        });
        loadView("table_tab_view.fxml");
    }

    @Override
    public void init() {
        flag.setGraphic(new ImageView(FLAG_IMAGE));

        flush.setOnAction(e -> checkChange(true));

        submit.setOnAction(e -> checkChange(false));


        tableView.changeStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue) && newValue) {
                setGraphic(flag);
            } else {
                setGraphic(null);
            }
        });

        numberTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                var text = numberTextField.getText();
                if (StringUtils.isEmpty(text)) {
                    numberTextField.setText(String.valueOf(pageSize));
                } else {
                    pageSize = Integer.parseInt(text);
                }
            }
        });

        next.setOnAction(e -> {
            pageIndex++;
            checkChange(true);
        });

        last.setOnAction(e -> {
            if (pageIndex > 1) {
                pageIndex--;
                checkChange(true);
            }
        });

        addData.setOnAction(e -> {
            if (updated()) {
                return;
            }
            var newData = FXCollections.<StringProperty>observableArrayList();
            metas.forEach(meta -> newData.add(new SimpleStringProperty(NULL)));
            tableView.addNewRow(newData);
            tableView.scrollTo(newData);
            tableView.getSelectionModel().select(newData);
        });

        delete.setOnAction(e -> {
            if (updated()) {
                return;
            }
            var selectIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectIndex == -1) {
                return;
            }
            var item = tableView.getSelectionModel().getSelectedItem();
            tableView.addDeleteItem(item);
        });

        //register shortcuts
        getTabPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            var tabPane = getTabPane();
            if (Objects.isNull(tabPane)) {
                return;
            }

            //If the changed tab is not the currently selected tab, it will not be changed
            var selectItem = tabPane.getSelectionModel();
            if (Objects.isNull(selectItem) || selectItem.getSelectedItem() != this) {
                return;
            }

            //fire sve event
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                event.consume();
                checkChange(false);
            }
            //search data in current table
            if (event.isControlDown() && event.getCode() == KeyCode.F && !tableView.getItems().isEmpty()) {
                borderPane.setTop(searchPopup);
            }
        });

        tableView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            if (index == -1) {
                indexCounter.setText("");
            } else {
                indexCounter.setText("第" + (index + 1) + "条记录");
            }
        });

        searchPopup.textChange(keyword -> {
            var items = tableView.getItems();
            var temp = TableDataUtils.findWithStr(items, keyword);
            searchList.clear();
            searchList.addAll(temp);
            //select first a cell
            if (searchList.size() > 0) {
                searchPopup.setIndexProperty(0);
            }
            return searchList.size();
        });

        searchPopup.indexPropertyProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            if (index == -1) {
                return;
            }
            var model = searchList.get(index);
            var columns = tableView.getColumns();
            var column = columns.get(model.getColumnIndex());
            //scroll target row
            tableView.scrollTo(model.getRowIndex());
            //scroll target column
            tableView.scrollToColumnIndex(model.getColumnIndex());
            //select target column
            tableView.getSelectionModel().select(model.getRowIndex(), column);
        });
        initTable();
    }

    private void initTable() {
        setLoading(true);
        var future = loadTableMeta().compose(v -> loadData()).compose(v -> countDataNumber());
        future.onComplete(v -> {
            if (v.succeeded()) {
                Platform.runLater(() -> {
                    tableView.refresh();
                    tableView.resetChange();
                });
            }
            setLoading(false);
        });

        future.onFailure(t -> DialogUtils.showErrorDialog(t, I18N.getString("databasefx.table.init.fail")));
    }


    private Future<Void> loadTableMeta() {
        var promise = Promise.<Void>promise();
        var future = pool.getDql().showColumns(model.getTable());
        future.onSuccess(metas ->
        {
            if (this.metas.size() > 0) {
                this.metas.clear();
            }
            this.metas.addAll(metas);

            //create column
            var columns = TableColumnUtils.createTableDataColumn(metas);
            Platform.runLater(() -> {
                tableView.getColumns().clear();
                tableView.getColumns().addAll(columns);
            });
            //get key
            var optional = MysqlHelper.getPrimaryKey(metas);
            if (optional.isPresent()) {
                tableView.setEditable(true);
                primaryKeyMeta = optional.get();
            }
            promise.complete();
        });
        future.onFailure(promise::fail);
        return promise.future();
    }

    /**
     * Paging load data
     */
    private Future<Void> loadData() {
        Platform.runLater(() -> tableView.getItems().clear());
        var promise = Promise.<Void>promise();
        var future = pool.getDql().query(model.getTable(), pageIndex, pageSize);
        future.onSuccess(rs -> {
            var list = FXCollections.<ObservableList<StringProperty>>observableArrayList();
            for (var values : rs) {
                var item = FXCollections.<StringProperty>observableArrayList();
                for (var val : values) {
                    item.add(new SimpleStringProperty(val));
                }
                list.add(item);
            }

            Platform.runLater(() -> {
                tableView.getItems().addAll(list);
                pageCounter.setText("于第" + pageIndex + "页");
            });
            promise.complete();
        });
        future.onFailure(promise::fail);
        return promise.future();
    }

    /**
     * Save changes
     *
     * @param isLoading Whether to refresh the current data list true false do not refresh
     */
    private void checkChange(boolean isLoading) {
        if (!tableView.isChangeStatus()) {
            initTable();
            return;
        }
        var result = DialogUtils.showAlertConfirm(I18N.getString("databasefx.table.update.tips"));
        // Synchronous data change to database
        if (result) {
            var dml = DATABASE_SOURCE.getDataBaseSource(model.getUuid()).getDml();
            var future = newData(dml).compose(rs -> updateData(dml)).compose(rs -> deleteData(dml));
            future.onSuccess(rs -> {
                Platform.runLater(() -> {
                    countDataNumber();
                    tableView.resetChange();
                    tableView.refresh();
                    if (isLoading) {
                        initTable();
                    }
                });
            });
            future.onFailure(t -> DialogUtils.showErrorDialog(t, I18N.getString("databasefx.table.update.fail")));
        } else {
            tableView.resetChange();
            initTable();
        }
    }

    /**
     * New data
     *
     * @param dml dml
     * @return Return new results
     */
    private Future<Integer> newData(DML dml) {
        var newRows = tableView.getNewRows();

        List<Future> futures = new ArrayList<>();

        for (ObservableList<StringProperty> newRow : newRows) {
            var columns = TableDataHelper.fxPropertyToObject(newRow);
            var future = dml.insert(metas, columns, model.getTable());
            var optional = dml.getAutoIncreaseField(metas);
            if (optional.isPresent()) {
                int i = metas.indexOf(optional.get());
                //Auto increment ID of callback processing after success
                future.setHandler(ar -> {
                    Platform.runLater(() -> {
                        int index = tableView.getItems().indexOf(newRow);
                        if (index != -1) {
                            var item = tableView.getItems().get(index);
                            item.set(i, new SimpleStringProperty(String.valueOf(ar.result())));
                        }
                    });
                });
            }
            futures.add(future);
        }
        var promise = Promise.<Integer>promise();
        var fut = CompositeFuture.all(futures);
        fut.setHandler(ar -> {
            if (ar.succeeded()) {
                promise.complete(futures.size());
                return;
            }
            promise.fail(ar.cause());
        });
        return promise.future();
    }

    /**
     * More detailed data
     *
     * @param dml dml
     * @return Return update results
     */
    private Future<Integer> updateData(DML dml) {
        var change = tableView.getChangeModes();
        //Update data
        if (change.size() > 0) {
            int keyIndex = metas.indexOf(primaryKeyMeta);
            //Due to asynchronous, you may only need to update in batch, but not in single update
            var values = TableDataHelper.getChangeValue(change, metas, keyIndex, tableView.getItems());
            //Update data asynchronously
            return dml.batchUpdate(values, model.getTable(), metas);
        }
        return Future.succeededFuture();
    }

    private Future<Integer> deleteData(DML dml) {
        var list = tableView.getDeletes();
        if (!list.isEmpty()) {
            var index = metas.indexOf(primaryKeyMeta);
            var keys = list.stream()
                    .map(it -> it.get(index)).map(TableDataHelper::singleFxPropertyToObject)
                    .toArray();
            return dml.batchDelete(primaryKeyMeta, keys, model.getTable());
        }
        return Future.succeededFuture();
    }


    private Future<Void> countDataNumber() {
        var promise = Promise.<Void>promise();
        var future = pool.getDql().count(model.getTable());
        future.onSuccess(number -> {
            Platform.runLater(() -> totalLabel.setText("(共" + number + "行)"));
            promise.complete();
        });
        future.onFailure(promise::fail);
        return promise.future();
    }

    /**
     * Determine whether the current table can be updated
     *
     * @return True can update false can not update
     */
    private boolean updated() {
        if (Objects.isNull(primaryKeyMeta)) {
            var tips = I18N.getString("databasefx.table.prohibit.update");
            DialogUtils.showNotification(tips, Pos.TOP_CENTER, NotificationType.WARNING);
            return true;
        }
        return false;
    }
}

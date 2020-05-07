package com.openjfx.database.app.component.impl;

import com.jfoenix.controls.JFXButton;
import com.openjfx.database.DML;
import com.openjfx.database.TableColumnMetaHelper;
import com.openjfx.database.app.TableDataHelper;
import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.component.TableDataCell;
import com.openjfx.database.app.component.TableDataView;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.model.TableDataChangeMode;
import com.openjfx.database.app.model.impl.TableTabModel;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.*;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;
import static com.openjfx.database.common.config.StringConstants.NULL;


/**
 * table tab
 *
 * @author yangkui
 * @since 1.0
 */
public class TableTab extends BaseTab<TableTabModel> {
    /***********************************************************
     *                    icon size                            *
     ***********************************************************/
    private static final double ICON_WIDTH = 0x14;
    private static final double ICON_HEIGHT = 0x14;
    /*************************************************************************************
     *                                图标信息                                             *
     *************************************************************************************/
    private static final Image ADD_DATA_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "add_data.png");
    private static final Image FLUSH_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "flush_icon.png");
    private static final Image NEXT_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "next_icon.png");
    private static final Image LAST_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "last_icon.png");
    private static final Image SUBMIT_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "save_icon.png");
    private static final Image DELETE_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "delete_icon.png");
    private static final Image FLAG_IMAGE = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "point.png");
    private static final Image CLOSE_IMAGE = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "close.png");
    /**
     * css样式路径
     */
    private static final String STYLE_SHEETS = "table_tab.css";
    /**********************************************************
     *                       连接池信息                         *
     **********************************************************/
    private final AbstractDataBasePool pool;
    private final Label flag = new Label();
    /**************************************************************
     *                          布局属性                           *
     **************************************************************/
    private final BorderPane borderPane = new BorderPane();
    private final TableDataView tableView = new TableDataView();

    private final VBox bottomBox = new VBox();
    private final HBox searchBox = new HBox();
    private final HBox actionBox = new HBox();

    /*******************************************************************
     *                       分页查询参数                                *
     *******************************************************************/

    private int pageIndex = 1;
    private int pageSize = 100;
    /*********************************************************************
     *                              控制按钮                               *
     *********************************************************************/
    private final JFXButton addData = new JFXButton();
    private final JFXButton flush = new JFXButton();
    private final JFXButton next = new JFXButton();
    private final JFXButton last = new JFXButton();
    private final JFXButton submit = new JFXButton();
    private final JFXButton delete = new JFXButton();
    private final TextField numberTextField = new TextField(String.valueOf(pageSize));
    private final TextField searchTextField = new TextField();
    private final List<TableColumnMeta> metas = new ArrayList<>();
    private final Label totalLabel = new Label("0行数据");
    /**
     * 当前表的key值
     */
    private TableColumnMeta keyMeta = null;

    public TableTab(TableTabModel model) {
        super(model);
        pool = DATABASE_SOURCE.getDataBaseSource(model.getUuid());
    }

    /**
     * 初始化数据
     */
    public void init() {
        //初始化图标
        flag.setGraphic(new ImageView(FLAG_IMAGE));
        addData.setGraphic(new ImageView(ADD_DATA_ICON));
        flush.setGraphic(new ImageView(FLUSH_ICON));
        next.setGraphic(new ImageView(NEXT_ICON));
        last.setGraphic(new ImageView(LAST_ICON));
        submit.setGraphic(new ImageView(SUBMIT_ICON));
        delete.setGraphic(new ImageView(DELETE_ICON));

        addData.setTooltip(new Tooltip("新增数据"));
        flush.setTooltip(new Tooltip("刷新"));
        submit.setTooltip(new Tooltip("保存更改"));
        delete.setTooltip(new Tooltip("删除"));


        var lBox = new HBox();
        var rBox = new HBox();

        var closeSearchBox = new JFXButton();

        var btGroup = new ButtonBar();
        var lastSearch = new Button();
        var nextSearch = new Button();


        lastSearch.setGraphic(new ImageView(LAST_ICON));
        nextSearch.setGraphic(new ImageView(NEXT_ICON));

        btGroup.getButtons().addAll(lastSearch, nextSearch);
        btGroup.setButtonMinWidth(50);

        closeSearchBox.setGraphic(new ImageView(CLOSE_IMAGE));

        lBox.getChildren().addAll(addData, delete, submit);
        rBox.getChildren().addAll(totalLabel, last, next, numberTextField, flush);

        HBox.setHgrow(rBox, Priority.ALWAYS);


        searchBox.getChildren().addAll(closeSearchBox, searchTextField, btGroup);
        actionBox.getChildren().addAll(lBox, rBox);

        bottomBox.getChildren().add(actionBox);

        searchBox.getStyleClass().add("bottom-box1");
        actionBox.getStyleClass().add("bottom-box2");

        bottomBox.getStyleClass().add("bottom-box");

        flush.setOnAction(e -> checkChange(true));

        borderPane.getStylesheets().add(AssetUtils.getCssStyle(STYLE_SHEETS));

        submit.setOnAction(e -> checkChange(false));

        closeSearchBox.setOnAction(e -> bottomBox.getChildren().remove(0));

        tableView.changeStatusProperty().addListener((observable, oldValue, newValue) -> {
            if (Objects.nonNull(newValue) && newValue) {
                setGraphic(flag);
            } else {
                setGraphic(null);
            }
        });

        numberTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String text = numberTextField.getText();
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
            if (!canUpdate()) {
                return;
            }
            ObservableList<StringProperty> newData = FXCollections.observableArrayList();
            metas.forEach(meta -> newData.add(new SimpleStringProperty(NULL)));
            tableView.addNewRow(newData);
            tableView.scrollTo(newData);
            tableView.getSelectionModel().select(newData);
        });

        delete.setOnAction(e -> {
            if (!canUpdate()) {
                return;
            }
            var selectIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectIndex == -1) {
                return;
            }
            var item = tableView.getSelectionModel().getSelectedItem();
            tableView.addDeleteItem(item);
        });

        //注册保存快捷键
        getTabPane().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            var tabPane = getTabPane();
            //如果变动的不是当前选中的tab不发生改变
            var selectItem = tabPane.getSelectionModel();
            if (Objects.isNull(selectItem) || selectItem.getSelectedItem() != this) {
                return;
            }
            //触发保存事件
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                event.consume();
                checkChange(false);
            }
            //搜索表格内的数据
            if (event.isControlDown() && event.getCode() == KeyCode.F && !tableView.getItems().isEmpty()) {
                bottomBox.getChildren().add(0, searchBox);
            }
        });

        borderPane.setCenter(tableView);
        borderPane.setBottom(bottomBox);

        setContent(borderPane);

        setText(model.getTableName());

        //设置tooltip 防止表名太长显示不全问题
        setTooltip(new Tooltip(model.getServerName() + "/" + model.getTableName()));

        loadTableMeta();
    }


    private void loadTableMeta() {
        Future<List<TableColumnMeta>> future = pool.getDql().showColumns(model.getTable());
        future.onSuccess(metas ->
        {
            int i = 0;
            for (TableColumnMeta meta : metas) {
                createColumn(i, meta.getField());
                i++;
            }
            if (this.metas.size() > 0) {
                this.metas.clear();
            }
            this.metas.addAll(metas);
            Optional<TableColumnMeta> optional = TableColumnMetaHelper.getTableKey(metas);
            if (optional.isPresent()) {
                tableView.setEditable(true);
                keyMeta = optional.get();
            }
            loadData();
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "获取表信息失败"));
    }

    private void createColumn(final int columnIndex, String title) {
        TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();
        column.setText(title);
        column.setCellValueFactory(cellDataFeatures -> {
            ObservableList<StringProperty> values = cellDataFeatures.getValue();
            if (columnIndex >= values.size()) {
                return new SimpleStringProperty("");
            } else {
                return cellDataFeatures.getValue().get(columnIndex);
            }
        });
        column.setCellFactory(TableDataCell.forTableColumn());
        Platform.runLater(() -> this.tableView.getColumns().add(column));
    }

    /**
     * 分页加载数据
     */
    private void loadData() {
        //清空之前的数据
        Platform.runLater(() -> tableView.getItems().clear());
        //加载数据
        Future<List<Object[]>> future = pool.getDql().query(model.getTable(), pageIndex, pageSize);
        future.onSuccess(rs -> {
            if (rs.isEmpty()) {
                Platform.runLater(() -> tableView.setPlaceholder(null));
                return;
            }

            List<ObservableList<StringProperty>> list = FXCollections.observableArrayList();

            for (Object[] objects : rs) {
                ObservableList<StringProperty> item = FXCollections.observableArrayList();

                for (Object object : objects) {
                    String val = object.toString();
                    item.add(new SimpleStringProperty(val));
                }

                list.add(item);
            }

            Platform.runLater(() -> {
                tableView.getItems().addAll(list);
                tableView.refresh();
                tableView.resetChange();
            });

            countDataNumber();
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "加载数据失败"));
    }

    /**
     * 保存更改数据
     *
     * @param isLoading 是否当前数据列表 true刷新 false不刷新
     */
    private void checkChange(boolean isLoading) {
        if (!tableView.isChangeStatus()) {
            loadData();
            return;
        }
        var result = DialogUtils.showAlertConfirm("检测到数据已经更新是否同步到数据库?");
        //同步改数据到数据库
        if (result) {
            var dml = DATABASE_SOURCE.getDataBaseSource(model.getUuid()).getDml();
            var future = newData(dml).compose(rs -> updateData(dml)).compose(rs -> deleteData(dml));
            future.onSuccess(rs -> {
                Platform.runLater(() -> {
                    countDataNumber();
                    tableView.resetChange();
                    tableView.refresh();
                    if (isLoading) {
                        loadData();
                    }
                });
            });
            future.onFailure(t -> DialogUtils.showErrorDialog(t, "更新失败"));
        } else {
            tableView.resetChange();
            loadData();
        }
    }

    /**
     * 新增数据
     *
     * @param dml dml
     * @return 返回新增结果
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
                //成功后回调处理自增id
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
        if (futures.size() > 0) {
            var fut = CompositeFuture.all(futures);
            fut.onSuccess(e -> promise.complete(futures.size()));
            fut.onFailure(promise::fail);
        }
        return promise.future();
    }

    /**
     * 更细数据
     *
     * @param dml dml
     * @return 返回更新结果
     */
    private Future<Integer> updateData(DML dml) {
        var change = tableView.getChangeModes();
        //更新数据
        if (change.size() > 0) {
            int keyIndex = metas.indexOf(keyMeta);

            //由于异步问题可能只能走批量更新,无法走单条更新
            var values = TableDataHelper.getChangeValue(change, metas, keyIndex, tableView.getItems());
            //异步更新数据
            return dml.batchUpdate(values, model.getTable(), metas);
        }
        return Future.succeededFuture();
    }

    private Future<Integer> deleteData(DML dml) {
        var list = tableView.getDeletes();
        if (!list.isEmpty()) {
            var index = metas.indexOf(keyMeta);
            var keys = list.stream()
                    .map(it -> it.get(index))
                    .map(TableDataHelper::singleFxPropertyToObject)
                    .toArray();
            return dml.batchDelete(keyMeta, keys, model.getTable());
        }
        return Future.succeededFuture();
    }


    private void countDataNumber() {
        var future = pool.getDql().count(model.getTable());
        future.onSuccess(number -> Platform.runLater(() -> totalLabel.setText(number + "行数据")));
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "统计数据失败"));
    }

    /**
     * 判断当前表是否能更新
     *
     * @return true能更新 false 不能更新
     */
    private boolean canUpdate() {
        if (Objects.isNull(keyMeta)) {
            DialogUtils.showNotification("当前设计表无Key,无法进行更新操作", Pos.TOP_CENTER, NotificationType.WARNING);
            return false;
        }
        return true;
    }
}

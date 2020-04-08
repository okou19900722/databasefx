package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.controls.MainTabPane;
import com.openjfx.database.app.controls.impl.TableTab;
import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.controls.impl.DBTreeNode;
import com.openjfx.database.app.controls.impl.TableTreeNode;
import com.openjfx.database.app.enums.MenuItemOrder;
import com.openjfx.database.app.enums.TabType;
import com.openjfx.database.app.model.BaseTabMode;
import com.openjfx.database.app.model.impl.TableTabModel;
import com.openjfx.database.app.stage.AboutStage;
import com.openjfx.database.app.stage.CreateConnectionStage;
import com.openjfx.database.app.stage.SQLEditStage;
import com.openjfx.database.app.utils.AlertUtils;
import com.openjfx.database.common.VertexUtils;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.ACTION;

/**
 * app主界面控制器
 *
 * @author yangkui
 * @since 1.0
 */
public class DatabaseFxController extends BaseController {

    /**
     * 顶部菜单栏
     */
    @FXML
    private MenuBar menuBar;

    @FXML
    private TreeView treeView;

    @FXML
    private TreeItem<String> treeItemRoot;

    @FXML
    private MainTabPane tabPane;

    @FXML
    private SplitPane splitPane;

    public static final String EVENT_ADDRESS = "controller:databaseFX";


    @Override
    public void init() {
        initDbList();
        //注册MenuItem上的点击事件
        ObservableList<Menu> menus = menuBar.getMenus();
        for (Menu menu : menus) {
            for (MenuItem item : menu.getItems()) {
                item.addEventHandler(ActionEvent.ACTION, event -> {
                    Object obj = event.getSource();
                    if (obj instanceof MenuItem) {
                        MenuItem temp = (MenuItem) obj;

                        Object userData = temp.getUserData();

                        if (Objects.nonNull(userData)) {
                            doExecMenuOrder(userData.toString());
                        }
                    }
                });
            }
        }

        ContextMenu menu = new ContextMenu();
        treeView.setContextMenu(menu);


        treeView.setOnContextMenuRequested(e -> {
            menu.getItems().clear();
            Object item = treeView.getSelectionModel().getSelectedItem();
            if (item instanceof BaseTreeNode) {
                menu.getItems().addAll(((BaseTreeNode) item).getMenus());
            }
        });

        treeView.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                Object selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (!(selectedItem instanceof TableTreeNode)) {
                    ((BaseTreeNode) selectedItem).init();
                } else {
                    //加载表数据
                    TableTreeNode tableTreeNode = ((TableTreeNode) selectedItem);
                    String uuid = tableTreeNode.getUuid() + "_" + tableTreeNode.getDatabase() + "_" + tableTreeNode.getValue();
                    TableTabModel model = new TableTabModel(uuid, tableTreeNode.getDatabase(), tableTreeNode.getValue());
                    addTab(uuid, model, TabType.TABLE);
                }
            }
        });

        //窗口关闭->关闭所有连接
        stage.setOnCloseRequest(e -> Platform.exit());

        VertexUtils.eventBus().consumer(EVENT_ADDRESS, this::eventBusHandler);
    }

    /**
     * 执行菜单指令集
     *
     * @param value 指令值
     */
    private void doExecMenuOrder(String value) {
        //构造指令
        MenuItemOrder order = MenuItemOrder.valueOf(value.toUpperCase());

        if (order == MenuItemOrder.CONNECTION) {
            new CreateConnectionStage();
        }
        if (order == MenuItemOrder.ABOUT) {
            new AboutStage();
        }
        if (order == MenuItemOrder.EXIT) {
            Platform.exit();
        }
        if (order == MenuItemOrder.EDITOR) {
            new SQLEditStage();
        }
        if (order == MenuItemOrder.FLUSH) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("你确定要刷新,刷新将断开所有连接!");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    tabPane.getTabs().clear();
                    DATABASE_SOURCE.closeAll();
                    initDbList();
                    JsonObject message = new JsonObject();
                    message.put(ACTION, MainTabPane.EventBusAction.CLEAR);
                    VertexUtils.eventBus().send(MainTabPane.EVENT_BUS_ADDRESS, message);
                }
            });
        }
    }

    /**
     * 渲染连接列表
     */
    private void initDbList() {
        //渲染数据库列表
        List<DBTreeNode> nodes = DbPreference.getParams()
                .stream().map(t -> new DBTreeNode(t.getUuid()))
                .collect(Collectors.toList());
        ObservableList<TreeItem<String>> observableList = treeItemRoot.getChildren();
        if (!observableList.isEmpty()) {
            observableList.clear();
        }
        treeItemRoot.getChildren().addAll(nodes);
    }

    private void addTab(String uuid, BaseTabMode mode, TabType tabType) {

        ObservableList<Tab> tabs = tabPane.getTabs();
        Optional<Tab> optional = tabs.stream()
                .filter(t -> t.getUserData().equals(uuid))
                .findAny();

        Tab tab = null;
        if (optional.isPresent()) {
            //切换tab
            int index = tabs.indexOf(optional.get());
            tab = tabs.get(index);
        } else {
            if (tabType == TabType.TABLE) {
                //新建tab
                TableTab tableTab = new TableTab((TableTabModel) mode);
                tableTab.setUserData(uuid);
                tabPane.getTabs().add(tableTab);
                tab = tableTab;
            }
        }
        if (Objects.nonNull(tab)) {
            tabPane.getSelectionModel().select(tab);
        }
    }

    /**
     * eventBus 统一处理外部输入信息
     *
     * @param message 消息内容
     */
    private void eventBusHandler(Message<JsonObject> message) {
        JsonObject body = message.body();

        EventBusAction action = EventBusAction.valueOf(body.getString(ACTION));

        String uuid = body.getString(Constants.UUID);
        //新增连接
        if (action == EventBusAction.ADD_CONNECTION) {
            DbPreference.getConnectionParam(uuid).ifPresent(db -> {
                DBTreeNode node = new DBTreeNode(uuid);
                Platform.runLater(() -> treeItemRoot.getChildren().add(node));
            });
        }
        //更新连接信息
        if (action == EventBusAction.UPDATE_CONNECTION) {
            ObservableList<TreeItem<String>> nodes = treeItemRoot.getChildren();
            nodes.stream().map(db -> ((BaseTreeNode) db))
                    .filter(db -> db.getUuid().equals(uuid))
                    .findAny()
                    .ifPresent(BaseTreeNode::flush);
        }
    }

    enum EventBusAction {
        /**
         * 新增连接
         */
        ADD_CONNECTION,
        /**
         * 更新连接信息
         */
        UPDATE_CONNECTION
    }
}

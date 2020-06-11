package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.component.SearchPopup;
import com.openjfx.database.app.component.tabs.DesignTableTab;
import com.openjfx.database.app.component.tabs.UserTab;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.component.MainTabPane;
import com.openjfx.database.app.component.tabs.TableTab;
import com.openjfx.database.app.controls.BaseTreeNode;
import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.controls.impl.*;
import com.openjfx.database.app.enums.MenuItemOrder;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.enums.TabType;
import com.openjfx.database.app.model.tab.BaseTabMode;
import com.openjfx.database.app.model.tab.meta.DesignTabModel;
import com.openjfx.database.app.model.tab.meta.TableTabModel;
import com.openjfx.database.app.model.tab.meta.UserTabModel;
import com.openjfx.database.app.stage.AboutStage;
import com.openjfx.database.app.stage.CreateConnectionStage;
import com.openjfx.database.app.stage.SQLEditStage;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.app.utils.EventBusUtils;
import com.openjfx.database.app.utils.TreeDataUtils;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.ACTION;
import static com.openjfx.database.app.config.Constants.SCHEME;

/**
 * App main interface controller
 *
 * @author yangkui
 * @since 1.0
 */
public class DatabaseFxController extends BaseController<Void> {
    /**
     * Top menu bar
     */
    @FXML
    private MenuBar menuBar;

    @FXML
    private VBox lBox;

    @FXML
    private TreeView<String> treeView;

    @FXML
    private TreeItem<String> treeItemRoot;

    @FXML
    private MainTabPane tabPane;

    @FXML
    private SplitPane splitPane;
    /**
     * search popup
     */
    private final SearchPopup searchPopup = SearchPopup.simplePopup();
    /**
     * search result list
     */
    private List<Integer> searchList = new ArrayList<>();
    /**
     * search select index
     */
    private int selectIndex = 0;

    /**
     * EVENT-BUS address
     */
    public static final String EVENT_ADDRESS = "controller:databaseFX";


    @Override
    public void init() {
        initDbList();
        //Register a click event on a MenuItem
        var menus = menuBar.getMenus();
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
        var menu = new ContextMenu();

        treeView.setOnContextMenuRequested(e -> {
            menu.getItems().clear();
            var item = treeView.getSelectionModel().getSelectedItem();
            if (item instanceof BaseTreeNode) {
                menu.getItems().addAll(((BaseTreeNode<String>) item).getMenus());
            }
        });

        treeView.setContextMenu(menu);

        VBox.setVgrow(treeView, Priority.ALWAYS);


        treeView.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                var selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    return;
                }
                var a = selectedItem instanceof TableTreeNode;
                var b = selectedItem instanceof TableViewTreeNode;
                if (a || b) {
                    //Load table data
                    var model = TableTabModel.build(selectedItem);
                    addTab(model, TabType.TABLE);
                } else if (selectedItem instanceof UserTreeNode) {
                    var model = UserTabModel.build((UserTreeNode) selectedItem);
                    addTab(model, TabType.DATABASE_USER);
                } else {
                    ((BaseTreeNode) selectedItem).init();
                }
            }
        });

        treeView.setOnKeyPressed(event -> {
            //search data in current tree view
            if (event.isControlDown() && event.getCode() == KeyCode.F) {
                lBox.getChildren().add(searchPopup);
            }
        });
        searchPopup.textChange(keyword -> {
            var cc = treeView.getRoot().getChildren();
            selectIndex = 0;
            searchList = TreeDataUtils.searchWithStr(cc, keyword);
            return searchList.size();
        });
        searchPopup.setSearchOnKeyPressed(event -> {
            //Skip to next search result
            if (event.getCode() == KeyCode.ENTER && !searchList.isEmpty()) {
                treeView.getSelectionModel().select(selectIndex);
                selectIndex++;
            }
        });
        searchPopup.setCloseHandler(event -> {
            searchList.clear();
            selectIndex = 0;
        });

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            var t = 800;
            var position = 0.2;
            if (newValue.doubleValue() > t) {
                position = 0.15;
            }
            splitPane.setDividerPosition(0, position);
        });
        //window close->close all connection
        stage.setOnCloseRequest(e -> Platform.exit());
        EventBusUtils.registerEventBus(EVENT_ADDRESS, this::eventBusHandler);
    }

    /**
     * execute menu order
     *
     * @param value order value
     */
    private void doExecMenuOrder(String value) {
        var order = MenuItemOrder.valueOf(value.toUpperCase());

        if (order == MenuItemOrder.CONNECTION) {
            createConnection();
        }
        if (order == MenuItemOrder.ABOUT) {
            new AboutStage();
        }
        if (order == MenuItemOrder.EXIT) {
            Platform.exit();
        }

        if (order == MenuItemOrder.FLUSH) {
            var result = DialogUtils.showAlertConfirm(resourceBundle.getString("controller.databasefx.flush.tips"));
            if (result) {
                tabPane.getTabs().clear();
                DATABASE_SOURCE.closeAll();
                initDbList();
                EventBusUtils.clearMainTab();
            }
        }
    }

    /**
     * render connection list
     */
    private void initDbList() {
        var nodes = DbPreference.getParams().stream().map(DBTreeNode::new).collect(Collectors.toList());
        var observableList = treeItemRoot.getChildren();
        if (!observableList.isEmpty()) {
            observableList.clear();
        }
        treeItemRoot.getChildren().addAll(nodes);
    }

    private void addTab(BaseTabMode mode, TabType tabType) {
        var tabs = tabPane.getTabs();
        var optional = tabs.stream().map(it -> (BaseTab) it)
                .filter(t -> t.getModel().getFlag().equals(mode.getFlag())).findAny();

        if (optional.isPresent()) {
            //change tab
            int index = tabs.indexOf(optional.get());
            tabPane.getSelectionModel().select(index);
            return;
        }
        final BaseTab tab;
        if (tabType == TabType.TABLE) {
            //create tab
            tab = new TableTab((TableTabModel) mode);
        } else if (tabType == TabType.DATABASE_USER) {
            tab = new UserTab((UserTabModel) mode);
        } else {
            tab = new DesignTableTab((DesignTabModel) mode);
        }

        Platform.runLater(() -> {
            tabPane.getTabs().add(tab);
            tab.init();
            tabPane.getSelectionModel().select(tab);
        });
    }

    /**
     * eventBus Unified processing of external input information
     *
     * @param message message body
     */
    private void eventBusHandler(Message<JsonObject> message) {
        var body = message.body();

        var action = EventBusAction.valueOf(body.getString(ACTION));

        var uuid = body.getString(Constants.UUID, "");
        //create connection
        if (action == EventBusAction.ADD_CONNECTION) {
            DbPreference.getConnectionParam(uuid).ifPresent(db -> {
                var node = new DBTreeNode(db);
                Platform.runLater(() -> treeItemRoot.getChildren().add(node));
            });
        }
        final var nodes = treeItemRoot.getChildren();
        //update connection
        if (action == EventBusAction.UPDATE_CONNECTION) {
            var optional = nodes.stream()
                    .map(db -> ((BaseTreeNode<String>) db)).filter(db -> db.getUuid().equals(uuid)).findAny();
            if (optional.isPresent()) {
                var node = optional.get();
                var optional1 = DbPreference.getConnectionParam(uuid);
                optional1.ifPresent(node::setParam);
            }
        }
        //flush scheme
        if (action == EventBusAction.FLUSH_SCHEME) {
            var optional = nodes.stream().map(db -> (BaseTreeNode<String>) db)
                    .filter(db -> db.getUuid().equals(uuid)).findAny();
            if (optional.isPresent()) {
                var item = optional.get();
                item.flush();
            }
        }

        //open design table
        if (action == EventBusAction.OPEN_DESIGN_TAB) {
            var model = DesignTabModel.build(body);
            addTab(model, TabType.DESIGN_TABLE);
        }
    }

    @FXML
    public void createQueryTerminal(ActionEvent event) {
        var item = treeView.getSelectionModel().getSelectedItem();
        if (item == null) {
            DialogUtils.showNotification(resourceBundle.getString("controller.databasefx.select.tips"), Pos.TOP_CENTER, NotificationType.INFORMATION);
            return;
        }
        var param = new JsonObject();
        if (item instanceof BaseTreeNode) {
            param.put(Constants.UUID, ((BaseTreeNode<String>) item).getUuid());
        }
        String scheme = "";
        if (item instanceof SchemeTreeNode) {
            scheme = item.getValue();
        }
        if (item instanceof TableTreeNode) {
            scheme = ((TableTreeNode) item).getScheme();
        }
        param.put(SCHEME, scheme);
        new SQLEditStage(param);
    }

    @FXML
    public void createConnection() {
        new CreateConnectionStage();
    }

    @FXML
    public void showDatabaseModelView() {
        DialogUtils.showAlertInfo(resourceBundle.getString("app.function.future"));
    }

    public enum EventBusAction {
        /**
         * add connection
         */
        ADD_CONNECTION,
        /**
         * update connection param
         */
        UPDATE_CONNECTION,
        /**
         * flush scheme
         */
        FLUSH_SCHEME,
        /**
         * open tab
         */
        OPEN_DESIGN_TAB
    }
}

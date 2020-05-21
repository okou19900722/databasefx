package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.component.DesignOptionBox;
import com.openjfx.database.app.config.Constants;
import com.openjfx.database.app.controls.DesignTableView;
import com.openjfx.database.app.model.DesignTableModel;
import com.openjfx.database.base.AbstractDataBasePool;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
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

    private AbstractDataBasePool pool;

    private final List<Button> actionList = new ArrayList<>();


    @Override
    public void init() {
        intiTable(fieldTable, DesignTableModel.class);
        initDataTable();
        for (Tab tab : tabPane.getTabs()) {
            tab.setClosable(false);
        }
        var i = 0;
        for (Node child : topBox.getChildren()) {
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
        });
        //listener fieldTable select change
        fieldTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var oIndex = oldValue.intValue();
            var index = newValue.intValue();
            //unbind last listener
            if (oIndex != -1) {
                var oItem = fieldTable.getItems().get(oIndex);
                var item = (DesignOptionBox) splitPane.getItems().get(1);
                oItem.setJson(item.getJsonResult());
            }
            if (index == -1) {
                return;
            }
            var item = fieldTable.getItems().get(index);
            box.updateValue(item.getJson());
        });

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            var t = 800;
            var position = 0.7;
            if (newValue.doubleValue() > t) {
                position = 0.8;
            }
            splitPane.setDividerPosition(0, position);
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
        var tableName = data.getString(Constants.TABLE_NAME, "");
        if ("".equals(tableName)) {
            return;
        }
        pool = DATABASE_SOURCE.getDataBaseSource(uuid);
        var future = pool.getDql().showColumns(tableName);
        future.onSuccess(rs -> {
            var list = DesignTableModel.build(rs);
            Platform.runLater(() -> fieldTable.setItems(FXCollections.observableList(list)));
        });
        future.onFailure(Throwable::printStackTrace);
    }

    private void listAction(final String ij) {
        //save
        if ("0_0".equals(ij)) {
            for (DesignTableModel item : fieldTable.getItems()) {
                System.out.println(item);
            }
        }
        //new create  row
        if ("0_1".equals(ij)) {
            var model = new DesignTableModel();
            var items = fieldTable.getItems();
            items.add(model);
            fieldTable.getSelectionModel().select(items.size() - 1);
        }
    }
}

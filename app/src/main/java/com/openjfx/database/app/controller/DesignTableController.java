package com.openjfx.database.app.controller;

import com.fasterxml.jackson.databind.deser.impl.PropertyValue;
import com.openjfx.database.DDL;
import com.openjfx.database.DQL;
import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.config.Constants.*;

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


    private final List<Button> actionList = new ArrayList<>();

    @Override
    public void init() {
        for (Tab tab : tabPane.getTabs()) {
            tab.setClosable(false);
        }
        var i = 0;
        for (Node child : topBox.getChildren()) {
            if (i != 0) {
                var button = (Button) child;
                actionList.add(button);
            }
            i++;
        }
        tabSelectChange(0);
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            var index = newValue.intValue();
            tabSelectChange(index);
        });
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
}

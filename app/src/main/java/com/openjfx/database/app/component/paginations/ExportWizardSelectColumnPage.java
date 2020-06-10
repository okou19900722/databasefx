package com.openjfx.database.app.component.paginations;

import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.model.ExportWizardModel;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.TableColumnMeta;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;
import static com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage.SelectColumnPattern.NORMAL;
import static com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage.SelectColumnPattern.SENIOR;

/**
 * export wizard select column page
 *
 * @author yangkui
 * @since 1.0
 */
public class ExportWizardSelectColumnPage extends BorderPane {
    /**
     * select column pattern
     *
     * @author yangkui
     * @since 1.0
     */
    public enum SelectColumnPattern {
        /**
         * normal
         */
        NORMAL,
        /**
         * senior
         */
        SENIOR
    }

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final ListView<NormalColumnNode> listView = new ListView<>();
    private final SQLEditor sqlEditor = new SQLEditor();
    private final ExportWizardModel model;
    private final Pagination pagination = new Pagination();
    private final VBox vBox = new VBox();

    public ExportWizardSelectColumnPage(ExportWizardModel model) {
        this.model = model;

        var topBox = new HBox();
        var bottomBox = new HBox();
        var normal = new RadioButton("常规");
        var senior = new RadioButton("高级");
        var entireSelect = new Button("全选");
        var cancelSelect = new Button("取消全选");

        normal.setUserData(NORMAL);
        senior.setUserData(SENIOR);
        VBox.setVgrow(listView, Priority.ALWAYS);
        vBox.getChildren().addAll(listView, bottomBox);
        bottomBox.getChildren().addAll(entireSelect, cancelSelect);

        toggleGroup.getToggles().addAll(normal, senior);

        topBox.getChildren().addAll(normal, senior);
        setTop(topBox);
        setCenter(pagination);
        updatePattern(model);
        pagination.setPageFactory((index) -> {
            final Node node;
            if (index == 0) {
                node = vBox;
            } else {
                node = sqlEditor;
            }
            return node;
        });

        bottomBox.getStyleClass().add("bottom-box");
        topBox.getStyleClass().add("top-box");
        getStyleClass().add("export-wizard-select-column-page");

        //register all select or cancel all select event
        cancelSelect.setOnAction(event -> listView.getItems().forEach(t -> t.select(false)));
        entireSelect.setOnAction(event -> listView.getItems().forEach(t -> t.select(true)));

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updatePattern(model));
        //Note that row must place after toggleGroup#selectToggleProperty
        updatePattern(model);
        //listener sql-editor text change
        sqlEditor.textProperty().addListener((observable, oldValue, newValue) -> model.setCustomExportSql(newValue));
    }

    private void updatePattern(ExportWizardModel model) {
        var selectItem = toggleGroup.getSelectedToggle();
        if (selectItem == null) {
            for (Toggle toggle : toggleGroup.getToggles()) {
                var userData = toggle.getUserData();
                if (userData == model.getSelectColumnPattern()) {
                    toggle.setSelected(true);
                    break;
                }
            }
            return;
        }
        var userData = selectItem.getUserData();
        if (userData == NORMAL) {
            pagination.setCurrentPageIndex(0);
            initTableColumn(model);
        } else {
            pagination.setCurrentPageIndex(1);
        }
        if (model.getSelectColumnPattern() != userData) {
            model.setSelectColumnPattern((SelectColumnPattern) userData);
        }
    }

    private void initTableColumn(ExportWizardModel model) {
        var pool = DATABASE_SOURCE.getDataBaseSource(model.getUuid());
        var dql = pool.getDql();
        var future = dql.showColumns(model.getScheme() + "." + model.getTable());
        future.onSuccess(ar -> {
            var items = ar.stream().map(NormalColumnNode::new).collect(Collectors.toList());
            Platform.runLater(() -> {
                listView.getItems().clear();
                listView.getItems().addAll(items);
            });
        });
        future.onFailure(t -> DialogUtils.showErrorDialog(t, "获取表列数据失败"));
    }

    /**
     * Dynamically change cache data based on user selected column state
     *
     * @param meta   change table column meta
     * @param status select/un-select
     */
    private void selectChange(TableColumnMeta meta, Boolean status) {
        var selectTableColumn = model.getSelectTableColumn();
        if (status) {
            if (!selectTableColumn.contains(meta)) {
                selectTableColumn.add(meta);
            }
        } else {
            selectTableColumn.remove(meta);
        }
    }

    private class NormalColumnNode extends HBox {
        private final CheckBox checkBox = new CheckBox();
        private final TableColumnMeta meta;

        public NormalColumnNode(TableColumnMeta meta) {
            this.meta = meta;

            var column = new Label();
            var hBox = new HBox();
            HBox.setHgrow(hBox, Priority.ALWAYS);
            column.setText(meta.getField());
            hBox.getChildren().add(column);
            getChildren().addAll(checkBox, hBox);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> selectChange(meta, newValue));
            getStyleClass().add("export-wizard-select-column-item");
        }

        //select or un-select CheckBox
        public void select(boolean is) {
            checkBox.setSelected(is);
            selectChange(meta, is);
        }
    }
}

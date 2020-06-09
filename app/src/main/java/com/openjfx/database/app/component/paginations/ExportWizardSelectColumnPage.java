package com.openjfx.database.app.component.paginations;

import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.model.ExportWizardModel;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.model.TableColumnMeta;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

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

    public ExportWizardSelectColumnPage(ExportWizardModel model) {
        this.model = model;

        var normal = new RadioButton("常规");
        var senior = new RadioButton("高级");
        var topBox = new HBox();

        normal.setUserData(NORMAL);
        senior.setUserData(SENIOR);

        toggleGroup.getToggles().addAll(normal, senior);
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> updatePattern(model));
        topBox.getChildren().addAll(normal, senior);
        setTop(topBox);
        updatePattern(model);
        topBox.getStyleClass().add("top-box");
        getStyleClass().add("export-wizard-select-column-page");
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
            setCenter(listView);
            initTableColumn(model);
        } else {
            setCenter(sqlEditor);
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
        if (status) {
            model.getSelectTableColumn().add(meta);
        } else {
            model.getSelectTableColumn().remove(meta);
        }
    }

    private class NormalColumnNode extends HBox {

        public NormalColumnNode(TableColumnMeta meta) {
            var column = new Label();
            var hBox = new HBox();
            HBox.setHgrow(hBox, Priority.ALWAYS);
            column.setText(meta.getField());
            hBox.getChildren().add(column);
            var checkBox = new CheckBox();
            getChildren().addAll(checkBox, hBox);
            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> selectChange(meta, newValue));
            getStyleClass().add("export-wizard-select-column-item");

        }
    }
}

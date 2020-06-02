package com.openjfx.database.app.controls;

import com.openjfx.database.app.model.TableDataChangeMode;
import com.openjfx.database.app.utils.TableCellUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.openjfx.database.common.config.StringConstants.NULL;


/**
 * table cell
 *
 * @author yangkui
 * @since 1.0
 */
public class TableDataCell extends TableCell<ObservableList<StringProperty>, String> {

    private TableTextField textField;

    /**
     * Null style
     */
    private final String NULL_STYLE = "null-style";
    /**
     * Value change style
     */
    private final String CHANGE_STYLE = "change-style";

    {
        //Do not wrap to prevent text from being too long
        setWrapText(false);
    }


    private final ObjectProperty<StringConverter<String>> converter = new SimpleObjectProperty<>();

    public TableDataCell(StringConverter<String> converter) {
        this.setConverter(converter);
    }

    public StringConverter<String> getConverter() {
        return converter.get();
    }

    public ObjectProperty<StringConverter<String>> converterProperty() {
        return converter;
    }

    public void setConverter(StringConverter<String> converter) {
        this.converter.set(converter);
    }

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty
                || Objects.isNull(item)
                || Objects.isNull(getTableView())
                || Objects.isNull(getTableRow())) {
            return;
        }
        TableCellUtils.updateItem(this, textField);

        if (item.equals(NULL)) {
            addClass(NULL_STYLE);
        } else {
            removeStyle(NULL_STYLE);
        }
        var dataView = (TableDataView) getTableView();

        int rowIndex = getTableRow().getIndex();
        int colIndex = getTableView().getColumns().indexOf(getTableColumn());

        var optional = dataView.getChangeModel(rowIndex, colIndex);

        if (optional.isPresent()) {
            addClass(CHANGE_STYLE);
        } else {
            removeStyle(CHANGE_STYLE);
        }
    }

    @Override
    public void startEdit() {

        if (!isEditable()
                || !getTableView().isEditable()
                || !getTableColumn().isEditable()) {
            return;
        }

        super.startEdit();

        if (isEditing()) {
            var column = (TableDataColumn) getTableColumn();
            if (textField == null) {
                textField = TableCellUtils.createTextField(this, column.getMeta());
            }
            TableCellUtils.startEdit(this, textField);
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        TableCellUtils.cancelEdit(this, null);
    }

    @Override
    public void commitEdit(String newValue) {
        String oldValue = getItem();
        var colIndex = getTableView().getEditingCell().getColumn();
        var rowIndex = getTableRow().getIndex();

        //Value change
        if (!oldValue.equals(newValue)) {
            var dataView = (TableDataView) getTableView();

            var optional = dataView.getChangeModel(rowIndex, colIndex);

            if (optional.isEmpty()) {
                var model = new TableDataChangeMode();
                model.setRowIndex(rowIndex);
                model.setColumnIndex(colIndex);
                model.setOriginalData(oldValue);
                model.setChangeData(newValue);
                dataView.addChangeMode(model);
                addClass(CHANGE_STYLE);
            } else {
                var model = optional.get();
                if (model.getOriginalData().equals(newValue)) {
                    dataView.removeChange(model);
                    removeStyle(CHANGE_STYLE);
                } else {
                    //Value update
                    model.setChangeData(newValue);
                }
            }
        }
        //Value change
        updateItem(newValue, false);
        super.commitEdit(newValue);
        //focus current tableCell
        getTableView().requestFocus();
        getTableView().getSelectionModel().select(rowIndex, getTableColumn());

    }

    public static Callback<TableColumn<ObservableList<StringProperty>, String>, TableCell<ObservableList<StringProperty>, String>> forTableColumn() {
        return list -> new TableDataCell(new DefaultStringConverter());
    }

    /**
     * Add style
     *
     * @param className Style name
     */
    private void addClass(String className) {
        boolean a = this.getStyleClass().contains(className);
        //If no style has been added before - > Add
        if (!a) {
            this.getStyleClass().add(className);
        }
    }

    /**
     * Move out style name
     *
     * @param className style name
     */
    private void removeStyle(String className) {
        ObservableList<String> list = getStyleClass();
        List<Integer> dd = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String cla = list.get(i);
            if (cla.equals(className)) {
                dd.add(i);
            }
        }
        //move out style
        for (Integer integer : dd) {
            list.remove(integer.intValue());
        }
    }
}

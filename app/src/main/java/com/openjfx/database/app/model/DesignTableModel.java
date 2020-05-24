package com.openjfx.database.app.model;

import com.openjfx.database.app.controls.EditChoiceBox;
import com.openjfx.database.common.MultipleHandler;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.stream.Collectors;

import static com.openjfx.database.app.DatabaseFX.DATABASE_SOURCE;

/**
 * design table model
 *
 * @author yangkui
 * @since 1.0
 */
public class DesignTableModel {
    /**
     * field name
     */
    private TextField field = new TextField();
    /**
     * field type
     */
    private EditChoiceBox<String> fieldType = new EditChoiceBox<>();
    /**
     * field length
     */
    private TextField fieldLength = new TextField();
    /**
     * field small point
     */
    private TextField fieldPoint = new TextField();
    /**
     * field is null?
     */
    private CheckBox nullable = new CheckBox();
    /**
     * field is virtual
     */
    private CheckBox virtual = new CheckBox();
    /**
     * field is key?
     */
    private CheckBox key = new CheckBox();
    /**
     * field comment
     */
    private TextField comment = new TextField();
    /**
     * default value
     */
    private final StringProperty defaultValue = new SimpleStringProperty();
    /**
     * field collation
     */
    private final StringProperty collation = new SimpleStringProperty();
    /**
     * field charset
     */
    private final StringProperty charset = new SimpleStringProperty();
    /**
     * field autoincrement?
     */
    private final BooleanProperty autoIncrement = new SimpleBooleanProperty();

    private MultipleHandler<String, String, Integer> callback;

    public DesignTableModel() {
        var dataTypeList = DATABASE_SOURCE.getDataType().getDataTypeList();
        fieldType.getItems().addAll(dataTypeList);
        field.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 0));
        fieldType.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 1));
        fieldLength.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 2));
        fieldPoint.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 3));
        nullable.selectedProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue.toString(), newValue.toString(), 4));
        virtual.selectedProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue.toString(), newValue.toString(), 5));
        key.selectedProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue.toString(), newValue.toString(), 6));
        comment.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 7));
        autoIncrement.addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue.toString(), newValue.toString(), 8));
        defaultValue.addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 9));
        charset.addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 10));
        collation.addListener((observable, oldValue, newValue) -> updateCallbackValue(oldValue, newValue, 11));

    }

    private void updateCallbackValue(String oldValue, String newValue, int columnIndex) {
        if (callback == null) {
            return;
        }
        callback.handler(oldValue, newValue, columnIndex);
    }

    public TextField getField() {
        return field;
    }

    public void setField(TextField field) {
        this.field = field;
    }

    public EditChoiceBox<String> getFieldType() {
        return fieldType;
    }

    public void setFieldType(EditChoiceBox<String> fieldType) {
        this.fieldType = fieldType;
    }

    public TextField getFieldLength() {
        return fieldLength;
    }

    public void setFieldLength(TextField fieldLength) {
        this.fieldLength = fieldLength;
    }

    public TextField getFieldPoint() {
        return fieldPoint;
    }

    public void setFieldPoint(TextField fieldPoint) {
        this.fieldPoint = fieldPoint;
    }

    public CheckBox getNullable() {
        return nullable;
    }

    public void setNullable(CheckBox nullable) {
        this.nullable = nullable;
    }

    public CheckBox getVirtual() {
        return virtual;
    }

    public void setVirtual(CheckBox virtual) {
        this.virtual = virtual;
    }

    public CheckBox getKey() {
        return key;
    }

    public void setKey(CheckBox key) {
        this.key = key;
    }

    public TextField getComment() {
        return comment;
    }

    public void setComment(TextField comment) {
        this.comment = comment;
    }

    public void setCallback(MultipleHandler<String, String, Integer> callback) {
        this.callback = callback;
    }

    public static DesignTableModel build(final TableColumnMeta meta) {
        var model = new DesignTableModel();
        var type = meta.getType();
        var charset = DATABASE_SOURCE.getCharset();
        var dataType = DATABASE_SOURCE.getDataType();

        model.getField().setText(meta.getField());
        model.getFieldType().setText(meta.getType());
        model.getNullable().setSelected("NO".equals(meta.getNull()));
        model.getComment().setText(meta.getComment());
        model.getKey().setSelected("PRI".equals(meta.getKey()));
        model.getFieldType().setText(dataType.getDataType(type));
        model.getFieldLength().setText(dataType.getDataTypeLength(type));
        model.getFieldPoint().setText(dataType.getDataFieldDecimalPoint(type));
        model.setDefaultValue(meta.getDefault());
        model.setCollation(meta.getCollation());
        model.setCharset(charset.getCharset(meta.getCollation()));
        model.setAutoIncrement(meta.getExtra().contains("auto_increment"));

        return model;
    }

    public static List<DesignTableModel> build(final List<TableColumnMeta> metas) {
        return metas.stream().map(DesignTableModel::build).collect(Collectors.toList());
    }

    public String getDefaultValue() {
        return defaultValue.get();
    }

    public StringProperty defaultValueProperty() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue.set(defaultValue);
    }

    public String getCollation() {
        return collation.get();
    }

    public StringProperty collationProperty() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation.set(collation);
    }

    public String getCharset() {
        return charset.get();
    }

    public StringProperty charsetProperty() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset.set(charset);
    }

    public boolean isAutoIncrement() {
        return autoIncrement.get();
    }

    public BooleanProperty autoIncrementProperty() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement.set(autoIncrement);
    }

    @Override
    public String toString() {
        return "DesignTableModel{" +
                "field=" + field +
                ", fieldType=" + fieldType +
                ", fieldLength=" + fieldLength +
                ", fieldPoint=" + fieldPoint +
                ", nullable=" + nullable +
                ", virtual=" + virtual +
                ", key=" + key +
                ", comment=" + comment +
                ", defaultValue=" + defaultValue +
                ", collation=" + collation +
                ", charset=" + charset +
                ", autoIncrement=" + autoIncrement +
                '}';
    }
}

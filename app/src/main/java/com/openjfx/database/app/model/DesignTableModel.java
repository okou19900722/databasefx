package com.openjfx.database.app.model;

import com.openjfx.database.app.controls.EditChoiceBox;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.json.JsonObject;
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
    private String defaultValue;
    /**
     * field collation
     */
    private String collation;
    /**
     * field charset
     */
    private String charset;
    /**
     * field autoincrement?
     */
    private boolean autoIncrement;


    public DesignTableModel() {
        var dataTypeList = DATABASE_SOURCE.getDataType().getDataTypeList();
        fieldType.getItems().addAll(dataTypeList);
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

    public static DesignTableModel build(final TableColumnMeta meta) {
        var model = new DesignTableModel();

        model.getField().setText(meta.getField());
        model.getFieldType().setText(meta.getType());
        model.getNullable().setSelected("NO".equals(meta.getNull()));
        model.getComment().setText(meta.getComment());
        model.getKey().setSelected("PRI".equals(meta.getKey()));

        var type = meta.getType();
        var charset = DATABASE_SOURCE.getCharset();
        var dataType = DATABASE_SOURCE.getDataType();
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
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCollation() {
        return collation;
    }

    public void setCollation(String collation) {
        this.collation = collation;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    @Override
    public String toString() {
        return "DesignTableModel{" +
                "field=" + field +
                ", fieldType=" + fieldType.getText() +
                ", fieldLength=" + fieldLength.getText() +
                ", fieldPoint=" + fieldPoint.getText() +
                ", nullable=" + nullable.isSelected() +
                ", virtual=" + virtual.isSelected() +
                ", key=" + key.isSelected() +
                ", comment=" + comment.getText() +
                ", defaultValue='" + defaultValue + '\'' +
                ", collation='" + collation + '\'' +
                ", charset='" + charset + '\'' +
                ", autoIncrement=" + autoIncrement +
                '}';
    }
}

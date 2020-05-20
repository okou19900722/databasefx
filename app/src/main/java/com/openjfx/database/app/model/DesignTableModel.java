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
     * field mark
     */
    private TextField mark = new TextField();
    /**
     * extension config info
     */
    private JsonObject json = new JsonObject();

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

    public JsonObject getJson() {
        return json;
    }

    public void setJson(JsonObject json) {
        this.json = json;
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

    public TextField getMark() {
        return mark;
    }

    public void setMark(TextField mark) {
        this.mark = mark;
    }

    public static DesignTableModel build(final TableColumnMeta meta) {
        var model = new DesignTableModel();

        model.getField().setText(meta.getField());
        model.getFieldType().setText(meta.getType());
        model.getNullable().setSelected("NO".equals(meta.getNull()));
        model.getMark().setText(meta.getComment());
        model.getKey().setSelected("PRI".equals(meta.getKey()));
        var type = meta.getType();

        var index = type.indexOf("(");
        if (index != -1) {
            var tt = type.substring(0, index);
            var length = type.substring(index + 1, type.length() - 1);
            model.getFieldType().setText(tt);
            model.getFieldLength().setText(length);
        }
        var json = new JsonObject();
        json.put("defaultValue", meta.getDefault());
        json.put("collation", meta.getCollation());
        json.put("charset", meta.getPrivileges());
        model.setJson(json);
        return model;
    }

    public static List<DesignTableModel> build(final List<TableColumnMeta> metas) {
        return metas.stream().map(DesignTableModel::build).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "DesignTableModel{" +
                "field=" + field.getText() +
                ", fieldType=" + fieldType.getText() +
                ", fieldLength=" + fieldLength.getText() +
                ", fieldPoint=" + fieldPoint.getText() +
                ", nullable=" + nullable.isSelected() +
                ", virtual=" + virtual.isSelected() +
                ", key=" + key.isSelected() +
                ", mark=" + mark.getText() +
                ", json=" + json +
                '}';
    }
}

package com.openjfx.database.app.model;

import com.openjfx.database.app.DatabaseFX;
import com.openjfx.database.app.controls.DataTypeCheckBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

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
    private DataTypeCheckBox fieldType = new DataTypeCheckBox(DatabaseFX.DATABASE_SOURCE.getCharset());
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

    public TextField getField() {
        return field;
    }

    public void setField(TextField field) {
        this.field = field;
    }

    public DataTypeCheckBox getFieldType() {
        return fieldType;
    }

    public void setFieldType(DataTypeCheckBox fieldType) {
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
}

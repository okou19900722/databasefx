package com.openjfx.database.app.model;

import com.openjfx.database.app.component.tabs.DesignTableTab;
import com.openjfx.database.app.controls.EditChoiceBox;
import com.openjfx.database.common.MultipleHandler;
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
    private final StringProperty defaultValue = new SimpleStringProperty("");
    /**
     * field collation
     */
    private final StringProperty collation = new SimpleStringProperty("");
    /**
     * field charset
     */
    private final StringProperty charset = new SimpleStringProperty("");
    /**
     * field autoincrement?
     */
    private final BooleanProperty autoIncrement = new SimpleBooleanProperty(false);

    private TableColumnMeta tableColumnMeta = null;

    private final DesignTableTab designTableTab;

    public DesignTableModel(DesignTableTab designTableTab) {
        this.designTableTab = designTableTab;
        var dataTypeList = DATABASE_SOURCE.getDataType().getDataTypeList();
        fieldType.getItems().addAll(dataTypeList);
        field.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.FIELD));
        fieldType.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.TYPE));
        fieldLength.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.LENGTH));
        fieldPoint.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.DECIMAL_POINT));
        nullable.selectedProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue.toString(), TableColumnMeta.TableColumnEnum.NULL));
        key.selectedProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue.toString(), TableColumnMeta.TableColumnEnum.PRIMARY_KEY));
        comment.textProperty().addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.COMMENT));
        autoIncrement.addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue.toString(), TableColumnMeta.TableColumnEnum.AUTO_INCREMENT));
        defaultValue.addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.DEFAULT));
        charset.addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.CHARSET));
        collation.addListener((observable, oldValue, newValue) -> updateCallbackValue(newValue, TableColumnMeta.TableColumnEnum.COLLATION));
    }

    private void updateCallbackValue(String newValue, TableColumnMeta.TableColumnEnum fieldName) {
        designTableTab.tableFieldChange(tableColumnMeta, this, fieldName, newValue);
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

    public static DesignTableModel build(final TableColumnMeta meta, final DesignTableTab tableTab) {
        var model = new DesignTableModel(tableTab);

        model.getField().setText(meta.getField());
        model.getFieldType().setText(meta.getType());
        model.getNullable().setSelected(!meta.getNull());
        model.getComment().setText(meta.getComment());
        model.getKey().setSelected(meta.getPrimaryKey());
        model.getFieldType().setText(meta.getType());
        model.getFieldLength().setText(meta.getLength());
        model.getFieldPoint().setText(meta.getDecimalPoint());
        model.setDefaultValue(meta.getDefault());
        model.setCollation(meta.getCollation());
        model.setCharset(meta.getCharset());
        model.setAutoIncrement(meta.getAutoIncrement());

        model.setTableColumnMeta(meta);

        return model;
    }

    public static List<DesignTableModel> build(final List<TableColumnMeta> metas, DesignTableTab designTableTab) {
        return metas.stream()
                .map(meta -> build(meta, designTableTab))
                .collect(Collectors.toList());
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

    public TableColumnMeta getTableColumnMeta() {
        return tableColumnMeta;
    }

    public void setTableColumnMeta(TableColumnMeta tableColumnMeta) {
        this.tableColumnMeta = tableColumnMeta;
    }

    @Override
    public String toString() {
        return "DesignTableModel{" +
                "field=" + field.getText() +
                ", fieldType=" + fieldType.getText() +
                ", fieldLength=" + fieldLength.getText() +
                ", fieldPoint=" + fieldPoint.getText() +
                ", nullable=" + nullable.getText() +
                ", key=" + key.getText() +
                ", comment=" + comment.getText() +
                ", defaultValue=" + defaultValue.get() +
                ", collation=" + collation.get() +
                ", charset=" + charset.get() +
                ", autoIncrement=" + autoIncrement.get() +
                '}';
    }
}

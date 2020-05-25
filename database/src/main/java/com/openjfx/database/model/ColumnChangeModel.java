package com.openjfx.database.model;

/**
 * column change detail
 *
 * @author yangkui
 * @since 1.0
 */
public class ColumnChangeModel {
    /**
     * origin value
     */
    private String originValue;
    /**
     * column index
     */
    private String fieldName;
    /**
     * new value
     */
    private String newValue;

    public String getOriginValue() {
        return originValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public void setOriginValue(String originValue) {
        this.originValue = originValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "ColumnChangeModel{" +
                "originValue='" + originValue + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", newValue='" + newValue + '\'' +
                '}';
    }
}

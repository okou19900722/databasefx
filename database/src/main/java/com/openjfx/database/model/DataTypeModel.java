package com.openjfx.database.model;

/**
 * each database support data type java model
 *
 * @author yangkui
 * @since 1.0
 */
public class DataTypeModel {
    /**
     * type name example for int、bigint、varchar etc
     */
    private String typeName;
    /**
     * current type belong to category,current category main contain thirty category NUMBER、STRING、DATETIME
     */
    private String category;
    /**
     * Whether there is decimal point
     */
    private boolean decimalPoint = false;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isDecimalPoint() {
        return decimalPoint;
    }

    public void setDecimalPoint(boolean decimalPoint) {
        this.decimalPoint = decimalPoint;
    }
}

package com.openjfx.database.app.model;

import com.openjfx.database.app.controller.DesignTableController;

/**
 * design table data model change record
 *
 * @author yangkui
 * @since 1.0
 */
public abstract class AbstractDesignTableChangeModel {

//    private final DesignTableController.DesignTableType designTableType;
//
//    public AbstractDesignTableChangeModel(DesignTableController.DesignTableType designTableType) {
//        this.designTableType = designTableType;
//    }
//
//    public DesignTableController.DesignTableType getDesignTableType() {
//        return designTableType;
//    }

    /**
     * add change
     *
     * @param rowIndex    change row index
     * @param columnIndex change column index
     * @param oldValue    change before value
     * @param newValue    change after value
     */
    public abstract void addChange(final int rowIndex, final int columnIndex, final String oldValue, final String newValue);

    /**
     * obtain change/create sql statement
     */
    public abstract void getSql();

    /**
     * change detail
     *
     * @author yangkui
     * @since 1.0
     */
    public static class ChangeModel {
        /**
         * origin value
         */
        private String originValue;
        /**
         * column index
         */
        private int columnIndex;
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

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        @Override
        public String toString() {
            return "ChangeModel{" +
                    "originValue='" + originValue + '\'' +
                    ", columnIndex=" + columnIndex +
                    ", newValue='" + newValue + '\'' +
                    '}';
        }
    }
}

package com.openjfx.database.app.model;

import com.openjfx.database.app.component.paginations.ExportWizardFormatPage;
import com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage;
import com.openjfx.database.model.TableColumnMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * export wizard model
 *
 * @author yangkui
 * @since 1.0
 */
public class ExportWizardModel {
    /**
     * uuid
     */
    private final String uuid;
    /**
     * scheme
     */
    private final String scheme;
    /**
     * table
     */
    private final String table;
    /**
     * custom export sql statement
     */
    private String customExportSql;
    /**
     * user select table column
     */
    private List<TableColumnMeta> selectTableColumn = new ArrayList<>();

    /**
     * export data format default txt
     */
    private ExportWizardFormatPage.ExportDataType exportDataType = ExportWizardFormatPage.ExportDataType.TXT;
    /**
     * export data select column pattern
     */
    private ExportWizardSelectColumnPage.SelectColumnPattern selectColumnPattern = ExportWizardSelectColumnPage.SelectColumnPattern.NORMAL;

    public ExportWizardModel(String uuid, String scheme, String table) {
        this.uuid = uuid;
        this.scheme = scheme;
        this.table = table;
    }

    public String getUuid() {
        return uuid;
    }

    public String getScheme() {
        return scheme;
    }

    public String getTable() {
        return table;
    }

    public String getCustomExportSql() {
        return customExportSql;
    }

    public void setCustomExportSql(String customExportSql) {
        this.customExportSql = customExportSql;
    }

    public List<TableColumnMeta> getSelectTableColumn() {
        return selectTableColumn;
    }

    public void setSelectTableColumn(List<TableColumnMeta> selectTableColumn) {
        this.selectTableColumn = selectTableColumn;
    }

    public ExportWizardFormatPage.ExportDataType getExportDataType() {
        return exportDataType;
    }

    public void setExportDataType(ExportWizardFormatPage.ExportDataType exportDataType) {
        this.exportDataType = exportDataType;
    }

    public ExportWizardSelectColumnPage.SelectColumnPattern getSelectColumnPattern() {
        return selectColumnPattern;
    }

    public void setSelectColumnPattern(ExportWizardSelectColumnPage.SelectColumnPattern selectColumnPattern) {
        this.selectColumnPattern = selectColumnPattern;
    }

    @Override
    public String toString() {
        return "ExportWizardModel{" +
                "uuid='" + uuid + '\'' +
                ", scheme='" + scheme + '\'' +
                ", table='" + table + '\'' +
                ", customExportSql='" + customExportSql + '\'' +
                ", selectTableColumn=" + selectTableColumn +
                ", exportDataType=" + exportDataType +
                ", selectColumnPattern=" + selectColumnPattern +
                '}';
    }
}

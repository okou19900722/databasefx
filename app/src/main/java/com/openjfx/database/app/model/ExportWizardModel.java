package com.openjfx.database.app.model;

import com.openjfx.database.app.component.paginations.ExportWizardFormatPage;
import com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage;

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

}

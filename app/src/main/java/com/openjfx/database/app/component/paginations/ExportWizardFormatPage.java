package com.openjfx.database.app.component.paginations;

import com.openjfx.database.app.model.ExportWizardModel;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import static com.openjfx.database.app.component.paginations.ExportWizardFormatPage.ExportDataType.*;

/**
 * @author yangkui
 * @since 1.0
 */
public class ExportWizardFormatPage extends BorderPane {

    public ExportWizardFormatPage(ExportWizardModel model) {

        var txt = new RadioButton("文本格式(*.txt)");
        var csv = new RadioButton("CSV文件(*.csv)");
        var html = new RadioButton("HTML文件(*.html;*.htm)");
        var excel = new RadioButton("Excel 文件(*.xls)");
        var excel1 = new RadioButton("Excel 文件(2007 或更高版本) (*.xlsx)");
        var sql = new RadioButton("SQL脚本(*.sql)");
        var xml = new RadioButton("XML 文件(*.xml)");
        var json = new RadioButton("JSON文件(*.json)");

        txt.setUserData(TXT);
        csv.setUserData(CSV);
        html.setUserData(HTML);
        excel.setUserData(EXCEL);
        excel1.setUserData(EXCEL_PRIOR);
        sql.setUserData(SQL_SCRIPT);
        xml.setUserData(XML);
        json.setUserData(JSON);

        var toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(txt, csv, html, excel, excel1, sql, xml, json);
        //init select value
        for (Toggle toggle : toggleGroup.getToggles()) {
            var type = toggle.getUserData();

            if (type == model.getExportDataType()) {
                toggle.setSelected(true);
                break;
            }
        }
        //listener select change
        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            var toggle = toggleGroup.getSelectedToggle();
            model.setExportDataType((ExportDataType) toggle.getUserData());
        });

        var vBox = new VBox();
        vBox.getChildren().addAll(txt, csv, html, excel, excel1, sql, xml, json);
        setCenter(vBox);
        getStyleClass().add("export-wizard-page");
    }

    /**
     * export data type enum
     *
     * @author yangkui
     * @since 1.0
     */
    public enum ExportDataType {
        /**
         * txt
         */
        TXT,
        /**
         * csv
         */
        CSV,
        /**
         * html
         */
        HTML,
        /**
         * excel
         */
        EXCEL,
        /**
         * excel(2007 later)
         */
        EXCEL_PRIOR,
        /**
         * SQL script
         */
        SQL_SCRIPT,
        /**
         * xml
         */
        XML,
        /**
         * JSON
         */
        JSON
    }
}

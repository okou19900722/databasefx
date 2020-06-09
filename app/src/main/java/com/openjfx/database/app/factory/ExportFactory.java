package com.openjfx.database.app.factory;

import com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage;
import com.openjfx.database.app.model.ExportWizardModel;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Factory class is used for data export
 *
 * @author yangkui
 * @since 1.0
 */
public class ExportFactory {
    /**
     * Export configuration information
     */
    private final ExportWizardModel model;
    /**
     * Export progress
     */
    private final DoubleProperty progress = new SimpleDoubleProperty(0.0);
    /**
     * Export progress description
     */
    private final StringProperty text = new SimpleStringProperty("");

    private ExportFactory(ExportWizardModel model) {
        this.model = model;
    }

    /**
     * start export task
     */
    public void start() {
        setText("开始检查导出条件.....");
        var a = model.getSelectColumnPattern() == ExportWizardSelectColumnPage.SelectColumnPattern.NORMAL
                && model.getSelectTableColumn().isEmpty();
        var b = model.getSelectColumnPattern() == ExportWizardSelectColumnPage.SelectColumnPattern.SENIOR
                && StringUtils.isEmpty(model.getCustomExportSql());
        if (a) {
            setText("常规模式至少选择一列!");
            return;
        }
        if (b) {
            setText("高级模式下SQL语句不能为空!");
            return;
        }
        setText(getModelText());
    }

    private String getModelText() {
        var sb = new StringBuilder();
        sb.append("---------------------------------------------\r\n");
        sb.append("Export format:").append(model.getExportDataType()).append("\r\n");
        sb.append("Select column model:").append(model.getSelectColumnPattern()).append("\r\n");
        if (model.getSelectColumnPattern() == ExportWizardSelectColumnPage.SelectColumnPattern.NORMAL) {
            sb.append("Export field:\r\n");
            for (TableColumnMeta tableColumnMeta : model.getSelectTableColumn()) {
                sb.append("              ").append(tableColumnMeta.getField()).append("\r\n");
            }
        } else {
            sb.append("Custom sql statement:\r\n");
            sb.append("                      ").append(model.getCustomExportSql()).append("\r\n");
        }
        sb.append("---------------------------------------------\r\n");
        return sb.toString();
    }

    public double getProgress() {
        return progress.get();
    }

    public DoubleProperty progressProperty() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public ExportWizardModel getModel() {
        return model;
    }

    public static ExportFactory factory(ExportWizardModel model) {
        return new ExportFactory(model);
    }
}

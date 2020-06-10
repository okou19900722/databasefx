package com.openjfx.database.app.factory;

import com.openjfx.database.app.DatabaseFX;
import com.openjfx.database.app.component.paginations.ExportWizardFormatPage;
import com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage;
import com.openjfx.database.app.model.ExportWizardModel;
import com.openjfx.database.base.AbstractDataBasePool;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.common.utils.StringUtils;
import com.openjfx.database.model.TableColumnMeta;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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
        setText("Start check export condition.....");
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
        var sql = buildSql();
        setText("Build SQL statement:");
        setText(" " + sql);
        setProgress(0.1);
        var pool = DatabaseFX.DATABASE_SOURCE.getDataBaseSource(model.getUuid());
        var future = pool.getPool().query(sql);
        future.onSuccess(rows -> {
            var map = new LinkedHashMap<String, List<String>>();
            var size = rows.columnsNames().size();
            for (Row row : rows) {
                for (int i = 0; i < size; i++) {
                    var val = StringUtils.getObjectStrElseGet(row.getValue(i), "");
                    var columnName = row.getColumnName(i);
                    final List<String> list;
                    if (map.containsKey(columnName)) {
                        list = map.get(columnName);

                    } else {
                        list = new ArrayList<>();
                        map.put(columnName, list);
                    }
                    list.add(val);
                }
                setProgress(0.8);
            }
            //execute export
            switch (model.getExportDataType()) {
                case JSON -> exportAsJson(map);
                case EXCEL -> exportAsExcel(map);
                case EXCEL_PRIOR -> exportAsSeniorExcel(map);
                case HTML -> exportAsHtml(map);
                default -> exportAsTxt(map);
            }
        });
        //execute sql fail
        future.onFailure(t -> setText(t.getMessage()));
    }

    private String getModelText() {
        var sb = new StringBuilder();
        sb.append("Export format:\r\n");
        sb.append(" ");
        sb.append(model.getExportDataType());
        sb.append("\r\n");
        sb.append("Select column model:\r\n");
        sb.append(" ");
        sb.append(model.getSelectColumnPattern());
        sb.append("\r\n");
        if (model.getSelectColumnPattern() == ExportWizardSelectColumnPage.SelectColumnPattern.NORMAL) {
            sb.append("Export field:\r\n");
            for (TableColumnMeta tableColumnMeta : model.getSelectTableColumn()) {
                sb.append(" ").append(tableColumnMeta.getField());
            }
        } else {
            sb.append("Custom sql statement:\r\n");
            sb.append(" ").append(model.getCustomExportSql());
        }
        return sb.toString();
    }

    /**
     * Export table as json
     *
     * @param map table data
     */
    private void exportAsJson(Map<String, List<String>> map) {
        var json = new JsonObject();
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            var values = entry.getValue();
            var array = new JsonArray();
            values.forEach(array::add);

            json.put(entry.getKey(), values);
        }
        writerFile(json.toBuffer().getBytes());
    }

    /**
     * Export table as excel(version after 2007)
     *
     * @param map table data
     */
    private void exportAsSeniorExcel(Map<String, List<String>> map) {
        var workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet(model.getTable());
        updateSheet(map, sheet);
        //writer file
        Throwable throwable = null;
        try {
            var file = new File(model.getPath());
            workbook.write(new FileOutputStream(file));
        } catch (IOException e) {
            throwable = e;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writerResult(throwable);
    }

    /**
     * Export table as excel(version before 2007)
     *
     * @param map table data
     */
    private void exportAsExcel(Map<String, List<String>> map) {
        var workbook = new HSSFWorkbook();
        var sheet = workbook.createSheet(model.getTable());
        updateSheet(map, sheet);
        //writer file
        Throwable throwable = null;
        try {
            var file = new File(model.getPath());
            workbook.write(file);
        } catch (IOException e) {
            throwable = e;
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writerResult(throwable);
    }

    /**
     * Encapsulation is used to unify versions before and after 07
     *
     * @param map   table data
     * @param sheet 07 after and before sheet
     */
    private void updateSheet(Map<String, List<String>> map, Sheet sheet) {
        var titleRow = sheet.createRow(0);
        var i = 0;
        //create title row
        for (String s : map.keySet()) {
            var cell = titleRow.createCell(i, CellType.STRING);
            cell.setCellValue(s);
            i++;
        }
        var list = new ArrayList<String>();
        var values = map.values();
        for (List<String> value : values) {
            list.addAll(value);
        }
        var rowSize = list.size() / map.size();
        for (int j = 0; j < rowSize; j++) {
            var row = sheet.createRow(j + 1);
            var k = 0;
            while (k < map.size()) {
                var cell = row.createCell(k, CellType.STRING);
                cell.setCellValue(list.get(k * rowSize));
                k++;
            }
        }
    }

    /**
     * Export table data as html
     *
     * @param map table data
     */
    private void exportAsHtml(Map<String, List<String>> map) {
        var templatePath = "assets/html/export_html_template.html";
        VertexUtils.getFileSystem().readFile(templatePath, ar -> {
            if (ar.failed()) {
                var str = "Export failed:\r\n html template file not found!";
                setText(str);
                return;
            }
            var title = "{{TABLE_TITLE}}";
            var content = "{{TABLE_CONTENT}}";
            var text = ar.result().toString().replace(title, model.getTable());
            var table = new StringBuilder();
            table.append("<tr>");
            for (String s : map.keySet()) {
                table.append("<th>").append(s).append("</th>");
            }
            table.append("</tr>");
            var values = map.values();
            var list = new ArrayList<String>();
            for (List<String> value : values) {
                list.addAll(value);
            }
            var rowSize = list.size() / map.size();
            for (int j = 0; j < rowSize; j++) {
                table.append("<tr>");
                var k = 0;
                while (k < map.size()) {
                    var td = list.get(k * rowSize);
                    table.append("<td>").append(td).append("</td>");
                    k++;
                }
                table.append("</tr>");
            }
            text = text.replace(content, table.toString());
            writerFile(text.getBytes());
        });
    }

    /**
     * Export table data as TXT
     *
     * @param map table data
     */
    private void exportAsTxt(Map<String, List<String>> map) {

    }

    private String buildSql() {
        final String sql;
        if (model.getSelectColumnPattern() == ExportWizardSelectColumnPage.SelectColumnPattern.SENIOR) {
            sql = model.getCustomExportSql();
        } else {
            var generator = DatabaseFX.DATABASE_SOURCE.getGenerator();
            var table = model.getScheme() + "." + model.getTable();
            sql = generator.select(model.getSelectTableColumn(), table);
        }
        return sql;
    }

    private void writerFile(byte[] bytes) {
        var path = model.getPath();
        var future = VertexUtils.writerFile(path, bytes);
        future.onComplete(ar -> writerResult(ar.cause()));
    }

    private void writerResult(Throwable throwable) {
        var str = "Export result:\r\n";
        if (throwable == null) {
            str += "  Export success file path:" + model.getPath();
            setProgress(1);
        } else {
            str += " Export failed cause:" + throwable.getMessage();
        }
        setText(str);
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

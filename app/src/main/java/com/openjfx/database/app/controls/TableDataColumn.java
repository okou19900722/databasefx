package com.openjfx.database.app.controls;

import com.openjfx.database.app.skin.TableColumnTooltipSkin;
import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * customer TableColumn
 *
 * @author yangkui
 * @since 1.0
 */
public class TableDataColumn extends TableColumn<ObservableList<StringProperty>, String> {
    /**
     * database table column meta data
     */
    private final TableColumnMeta meta;

    private static final String[] NUMBER = new String[]{
            "tinyint", "smallint", "mediumint", "int", "bigint"
    };
    private static final String[] DATETIME = new String[]{
            "DATETIME", "TIMESTAMP", "DATE", "TIME", "YEAR"
    };

    private static final Image LETTER_ICON = getLocalImage(18, 18, "letter-icon.png");
    private static final Image NUMBER_ICON = getLocalImage(18, 18, "number-icon.png");
    private static final Image TIME_ICON = getLocalImage(18, 18, "time-icon.png");


    public TableDataColumn(TableColumnMeta meta) {
        this.meta = meta;
        initLabel();
        setText(meta.getField());
    }

    public static final class TableColumnTooltip extends Tooltip {
        private final ObjectProperty<TableColumnMeta> tableColumnMetaObjectProperty = new SimpleObjectProperty<>();

        public TableColumnTooltip(TableColumnMeta meta) {
            tableColumnMetaObjectProperty.set(meta);
        }

        @Override
        protected Skin<?> createDefaultSkin() {
            return new TableColumnTooltipSkin(this);
        }

        public TableColumnMeta getTableColumnMetaObjectProperty() {
            return tableColumnMetaObjectProperty.get();
        }

        public ObjectProperty<TableColumnMeta> tableColumnMetaObjectPropertyProperty() {
            return tableColumnMetaObjectProperty;
        }

        public void setTableColumnMetaObjectProperty(TableColumnMeta tableColumnMetaObjectProperty) {
            this.tableColumnMetaObjectProperty.set(tableColumnMetaObjectProperty);
        }
    }

    private void initLabel() {

        var label = new Label();
        final ImageView imageView;
        if (isFixType(DATETIME)) {
            imageView = new ImageView(TIME_ICON);
        } else if (isFixType(NUMBER)) {
            imageView = new ImageView(NUMBER_ICON);
        } else {
            imageView = new ImageView(LETTER_ICON);
        }
        label.setGraphic(imageView);
        setGraphic(label);
        label.setTooltip(new TableColumnTooltip(meta));
    }

    private boolean isFixType(final String[] ss) {
        var type = meta.getType().toLowerCase();
        for (String s : ss) {
            if (type.startsWith(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}

package com.openjfx.database.app;

import com.openjfx.database.app.model.TableDataChangeMode;
import com.openjfx.database.model.TableColumnMeta;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.openjfx.database.common.config.StringConstants.*;

public class TableDataHelper {
    /**
     * Convert the changed field to map
     *
     * @param modes update value
     * @param metas table column meta
     * @param data  raw data
     * @return Return converted data
     */
    public static List<Map<String, Object[]>> getChangeValue(List<TableDataChangeMode> modes, List<TableColumnMeta> metas, int keyIndex, ObservableList<ObservableList<StringProperty>> data) {
        var rows = modes.stream().map(TableDataChangeMode::getRowIndex)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        var list = new ArrayList<Map<String, Object[]>>();
        for (var row : rows) {
            //Get row data
            var item = data.get(row);
            var objects = fxPropertyToObject(item);
            var map = new HashMap<String, Object[]>();
            var models = modes.stream().filter(m -> m.getRowIndex() == row).collect(Collectors.toList());
            //The default key value has not changed
            map.put(KEY, new Object[]{objects[keyIndex]});
            //Check whether the key value changes
            for (var model : models) {
                int colIndex = model.getColumnIndex();
                if (keyIndex == colIndex) {
                    map.put(KEY, new Object[]{model.getOriginalData()});
                    break;
                }
            }
            map.put(ROW, objects);
            list.add(map);
        }
        return list;
    }

    public static Object[] fxPropertyToObject(ObservableList<StringProperty> items) {
        var obj = new Object[items.size()];
        for (int i = 0; i < items.size(); i++) {
            var object = items.get(i);
            obj[i] = singleFxPropertyToObject(object);
        }
        return obj;
    }

    public static Object singleFxPropertyToObject(StringProperty item) {
        var object = item.getValue();
        final Object target;
        if (!object.equals(NULL)) {
            target = object;
        } else {
            target = null;
        }
        return target;
    }
}

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
     * Replace the FX attribute value with the normal value
     *
     * @param list List of property values
     * @return Return to the list of general values
     */
    public static List<Object[]> fxPropertyToObject(List<ObservableList<StringProperty>> list) {
        return list.stream()
                .map(TableDataHelper::fxPropertyToObject)
                .collect(Collectors.toList());
    }

    /**
     * Convert the changed field to map
     *
     * @param modes update value
     * @param metas table column meta
     * @param data  raw data
     * @return Return converted data
     */
    public static List<Map<String, Object[]>> getChangeValue(List<TableDataChangeMode> modes, List<TableColumnMeta> metas, int keyIndex, ObservableList<ObservableList<StringProperty>> data) {
        List<Integer> rows = modes.stream().map(TableDataChangeMode::getRowIndex)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<Map<String, Object[]>> list = new ArrayList<>();

        for (Integer row : rows) {
            //Get row data
            ObservableList<StringProperty> item = data.get(row);

            Object[] objects = fxPropertyToObject(item);

            Map<String, Object[]> map = new HashMap<>();

            List<TableDataChangeMode> models = modes.stream()
                    .filter(m -> m.getRowIndex() == row)
                    .collect(Collectors.toList());
            //The default key value has not changed
            map.put(KEY, new Object[]{objects[keyIndex]});
            //Check whether the key value changes
            for (TableDataChangeMode model : models) {
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
        Object[] obj = new Object[items.size()];
        for (int i = 0; i < items.size(); i++) {
            StringProperty object = items.get(i);
            obj[i] = singleFxPropertyToObject(object);
        }
        return obj;
    }

    public static Object singleFxPropertyToObject(StringProperty item) {
        Object object = item.getValue();
        Object target = null;
        if (!object.equals(NULL)) {
            target = object;
        }
        return target;
    }
}

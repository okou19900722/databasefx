package com.openjfx.database.app.model.impl;

import com.openjfx.database.app.model.AbstractDesignTableChangeModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yangkui
 * @since 1.0
 */
public class RegularFieldTableChangeModel extends AbstractDesignTableChangeModel {
    /**
     * cached all field change
     */
    private final Map<Integer, List<AbstractDesignTableChangeModel.ChangeModel>> updateMap = new HashMap<>();

//    public RegularFieldTableChangeModel(DesignTableController.DesignTableType designTableType) {
//        super(designTableType);
//    }

    @Override
    public void addChange(int rowIndex, int columnIndex, String oldValue, String newValue) {
        var row = updateMap.get(rowIndex);
        row = (row == null ? new ArrayList<>() : row);
        var optional = row.stream().filter(column -> column.getColumnIndex() == columnIndex).findAny();
        if (optional.isPresent()) {
            var model = optional.get();
            //value not happen change
            if (model.getOriginValue().equals(newValue)) {
                row.remove(model);
                if (row.isEmpty()) {
                    updateMap.remove(rowIndex);
                }
            } else {
                //update value
                model.setNewValue(newValue);
            }
        }
        var a = updateMap.containsKey(rowIndex);
        if (!a || optional.isEmpty()) {
            var model = new ChangeModel();
            model.setColumnIndex(columnIndex);
            model.setOriginValue(oldValue);
            model.setNewValue(newValue);
            if (!a) {
                var list = new ArrayList<ChangeModel>();
                list.add(model);
                updateMap.put(rowIndex, list);
            }
            if (optional.isEmpty()) {
                row.add(model);
            }
        }

    }

    @Override
    public void getSql() {

    }

    @Override
    public String toString() {
        return "RegularFieldTableChangeModel{" +
                "updateMap=" + updateMap +
                '}';
    }
}

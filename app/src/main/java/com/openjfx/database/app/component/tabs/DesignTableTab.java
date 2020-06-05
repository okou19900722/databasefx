package com.openjfx.database.app.component.tabs;


import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.model.tab.meta.DesignTabModel;


/**
 * Design table tab
 *
 * @author yangkui
 * @since 1.0
 */
public class DesignTableTab extends BaseTab<DesignTabModel> {
    public DesignTableTab(DesignTabModel model) {
        super(model);
        loadView("design_tab_view.fxml");
    }

    @Override
    public void init() {

    }
}

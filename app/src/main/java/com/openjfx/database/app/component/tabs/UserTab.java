package com.openjfx.database.app.component.tabs;

import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.model.tab.meta.UserTabModel;
import com.openjfx.database.app.utils.AssetUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

/**
 * database user tab
 *
 * @author yangkui
 * @since 1.0
 */
public class UserTab extends BaseTab<UserTabModel> {
    @FXML
    private TabPane tabPane;

    /**
     * user icon
     */
    private final static Image USER_ICON = AssetUtils.getLocalImage(20, 20, "user_icon.png");

    public UserTab(UserTabModel model) {
        super(model);
        var title = model.getUser() + "(" + model.getServerName() + ")";
        setTabIcon(USER_ICON);
        setText(title);
        setTooltip(new Tooltip(title));
        loadView("user_tab_view.fxml");
    }

    @Override
    public void init() {

    }

}

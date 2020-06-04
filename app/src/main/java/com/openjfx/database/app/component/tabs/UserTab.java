package com.openjfx.database.app.component.tabs;

import com.openjfx.database.app.component.BaseTab;
import com.openjfx.database.app.model.impl.UserTabModel;
import com.openjfx.database.app.utils.AssetUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;

import java.io.IOException;

import static com.openjfx.database.app.DatabaseFX.I18N;

/**
 * database user tab
 *
 * @author yangkui
 * @since 1.0
 */
public class UserTab extends BaseTab<UserTabModel> {
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
        try {
            Parent root = FXMLLoader.load(ClassLoader.getSystemResource("fxml/component/user_tab_view.fxml"), I18N);
            setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {

    }
//
//    @Override
//    public void updateValue(boolean t) {
//        if (Objects.nonNull(getText())) {
//            var a = getText().contains("/");
//            var b = !t && !a || t && a;
//            if (b) {
//                return;
//            }
//        }
//        var name = model.getUser();
//        if (t) {
//            name = model.getHost() + "/" + name;
//        }
//        setText(name);
//    }
}

package com.openjfx.database.app.stage;

import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;

/**
 *
 *
 * 关于视图
 *
 * @author yangkui
 * @since 1.0
 *
 */
@Layout(layout = "about_view.fxml",width = 500,
        height = 400, resizable = false,
        await = true,title = "关于我们")
public class AboutStage extends BaseStage {
}

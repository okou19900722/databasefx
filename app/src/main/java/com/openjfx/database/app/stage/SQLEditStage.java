package com.openjfx.database.app.stage;

import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;
import com.openjfx.database.model.ConnectionParam;

/**
 * sql编辑器
 *
 * @author yangkui
 * @since 1.0
 *
 */
@Layout(layout = "sql_edit_view.fxml",title = "SQL编辑器")
public class SQLEditStage extends BaseStage<ConnectionParam> {
    public SQLEditStage(ConnectionParam data) {
        super(data);
    }
}

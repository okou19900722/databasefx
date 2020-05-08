package com.openjfx.database.app.stage;

import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.stage.Modality;

/**
 * sql编辑器
 *
 * @author yangkui
 * @since 1.0
 */
@Layout(layout = "sql_edit_view.fxml", title = "SQL编辑器", modality = Modality.APPLICATION_MODAL)
public class SQLEditStage extends BaseStage<JsonObject> {
    public SQLEditStage(JsonObject data) {
        super(data);
    }
}

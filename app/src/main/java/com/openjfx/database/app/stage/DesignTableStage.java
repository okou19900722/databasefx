package com.openjfx.database.app.stage;

import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;
import io.vertx.core.json.JsonObject;

/**
 *
 *
 * design stage view
 *
 * @author yangkui
 * @since 1.0
 */
@Layout(layout = "design_table_view.fxml",title = "设计表")
public class DesignTableStage extends BaseStage<JsonObject> {
    public DesignTableStage(JsonObject data) {
        super(data);
    }
}

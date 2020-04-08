package com.openjfx.database.app.stage;

import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;
import io.vertx.core.json.JsonObject;

/**
 * 生成sql语句
 *
 * @author yangkui
 * @since 1.0
 */
@Layout(layout = "sql_gen_view.fxml", title = "生成sql",await = true)
public class SQLGenStage extends BaseStage<JsonObject> {
    public SQLGenStage(JsonObject data) {
        super(data);
    }
}

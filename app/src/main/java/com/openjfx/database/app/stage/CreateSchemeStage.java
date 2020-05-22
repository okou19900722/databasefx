package com.openjfx.database.app.stage;

import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;

/**
 * New database stage
 *
 * @author yangkui
 * @since 1.0
 */
@Layout(layout = "create_scheme_view.fxml", resizable = false)
public class CreateSchemeStage extends BaseStage<String> {
    public CreateSchemeStage(String data) {
        super(data);
    }
}

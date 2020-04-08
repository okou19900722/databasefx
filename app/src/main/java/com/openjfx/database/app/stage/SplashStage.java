package com.openjfx.database.app.stage;

import com.jfoenix.controls.JFXSlider;
import com.openjfx.database.app.BaseStage;
import com.openjfx.database.app.annotation.Layout;
import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


import static com.openjfx.database.app.utils.AssetUtils.getCssStyle;
import static com.openjfx.database.app.utils.DialogUtils.showErrorDialog;
import static com.openjfx.database.app.config.FileConfig.loadConfig;
import static com.openjfx.database.app.config.Constants.*;


/**
 * app启动屏
 *
 * @author yangkui
 * @since 1.0
 */
@Layout(layout = "splash_view.fxml",
        width = 600, height = 400,
        stageStyle = StageStyle.UNDECORATED)
public class SplashStage extends BaseStage { }

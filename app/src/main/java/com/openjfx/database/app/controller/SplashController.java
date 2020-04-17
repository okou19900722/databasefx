package com.openjfx.database.app.controller;

import com.jfoenix.controls.JFXSlider;
import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.stage.DatabaseFxStage;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.config.FileConfig.loadConfig;
import static com.openjfx.database.app.utils.DialogUtils.showErrorDialog;

/**
 *
 *
 * Splash 启动页控制器
 *
 * @author yangkui
 * @since 1.0
 *
 */
public class SplashController extends BaseController {
    @FXML
    private Label title;

    @FXML
    private JFXSlider progress;

    @Override
    public void init() {
        loadAppConfig();
    }

    /**
     * 加载app配置信息
     */
    private void loadAppConfig() {
        CompletableFuture.runAsync(() -> {
            try {
                AssetUtils.loadAllFont();
                updateProgress("初始化...", 0);
                Thread.sleep(250);
                updateProgress("加载数据库配置", 25);
                JsonObject db = loadConfig(DB_CONFIG_FILE);
                Thread.sleep(250);
                updateProgress("配置数据库...", 50);
                List<ConnectionParam> params = db.getJsonArray(DATABASE)
                        .stream()
                        .map(it -> ((JsonObject) it).mapTo(ConnectionParam.class))
                        .collect(Collectors.toList());
                DbPreference.setParams(params);
                Thread.sleep(250);
                updateProgress("加载app配置", 75);
                loadConfig(UI_CONFIG_FILE);
                Thread.sleep(250);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }).whenComplete((r, t) -> {
            if (Objects.isNull(t)) {
                updateProgress("加载成功", 100);
                Platform.runLater(DatabaseFxStage::new);
            } else {
                showErrorDialog(t, "初始化失败");
            }
            Platform.runLater(stage::close);
        });
    }

    /**
     * 更新进度
     *
     * @param title 进度描述
     * @param value 进度值 0-100
     */
    private void updateProgress(String title, double value) {
        Platform.runLater(() -> {
            this.title.setText(title);
            progress.setValue(value);
        });
    }
}

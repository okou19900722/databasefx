package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.stage.DatabaseFxStage;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.app.config.FileConfig.loadConfig;
import static com.openjfx.database.app.utils.DialogUtils.showErrorDialog;

/**
 * Splash stage controller
 *
 * @author yangkui
 * @since 1.0
 */
public class SplashController extends BaseController<Void> {
    @FXML
    private Label title;

    @Override
    public void init() {
        var future = CompletableFuture.runAsync(() -> {
            try {
                init0();
                init1();
                init2();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
        future.whenComplete((r, t) -> {
            if (Objects.nonNull(t)) {
                System.out.println("application startup failed cause:" + t.getMessage());
                showErrorDialog(t, "启动失败");
                return;
            }
            updateProgress("启动成功");
            Platform.runLater(() -> {
                new DatabaseFxStage();
                stage.close();
            });
        });
    }


    /**
     * update progress
     *
     * @param title progress describe
     */
    private void updateProgress(String title) {
        Platform.runLater(() -> this.title.setText(title));
    }

    private void init0() throws Exception {
        AssetUtils.loadAllFont();
        updateProgress("初始化中....");
        Thread.sleep(250);
    }

    private void init1() throws InterruptedException {
        updateProgress("加载配置信息...");
        var db = loadConfig(DB_CONFIG_FILE);
        var params = db.getJsonArray(DATABASE).stream()
                .map(it -> ((JsonObject) it).mapTo(ConnectionParam.class))
                .collect(Collectors.toList());
        DbPreference.setParams(params);
        Thread.sleep(250);
    }

    private void init2() throws InterruptedException {
        updateProgress("加载UI配置....");
        loadConfig(UI_CONFIG_FILE);
        Thread.sleep(250);
    }
}

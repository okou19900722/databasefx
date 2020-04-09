package com.openjfx.database.app.component;

import com.openjfx.database.app.component.impl.TableTab;
import com.openjfx.database.common.VertexUtils;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.openjfx.database.app.config.Constants.ACTION;
import static com.openjfx.database.app.config.Constants.UUID;

/**
 * 主界面TabPane
 *
 * @author yangkui
 * @since 1.0
 */
public class MainTabPane extends TabPane {
    /**
     * event-bus 地址
     */
    public static final String EVENT_BUS_ADDRESS = "controls:mainTabPane";

    public MainTabPane() {
        registerEventBus();
    }

    private void registerEventBus() {
        VertexUtils.eventBus().<JsonObject>consumer(EVENT_BUS_ADDRESS, handler -> {
            JsonObject body = handler.body();
            String action = body.getString(ACTION);
            String uuid = body.getString(UUID);
            EventBusAction busAction = EventBusAction.valueOf(action);
            final List<Tab> tabs = new ArrayList<>();
            //移出一个Tab
            if (busAction == EventBusAction.REMOVE) {
                Optional<Tab> optional = this.getTabs().stream().filter(
                        it -> ((TableTab) it).getModel().getRawUUid().equals(uuid)
                ).findAny();
                optional.ifPresent(tabs::add);
            }
            //移出多个Tab
            if (busAction == EventBusAction.REMOVE_MANY) {
                List<Tab> tt = this.getTabs().stream().filter(
                        it -> ((TableTab) it).getModel().getRawUUid().startsWith(uuid)
                ).collect(Collectors.toList());
                tabs.addAll(tt);
            }
            //清空tab
            if (busAction == EventBusAction.CLEAR) {
                tabs.addAll(getTabs());
            }
            if (!tabs.isEmpty()){
                Platform.runLater(()->getTabs().removeAll(tabs));
            }
        });
    }

    /**
     * 消息类型
     */
   public enum EventBusAction {
        /**
         * 移出单个Tab
         */
        REMOVE,
        /**
         * 移出指定Tab
         */
        REMOVE_MANY,
        /**
         * 移出所有Tab
         */
        CLEAR
    }
}

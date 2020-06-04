package com.openjfx.database.app.component;

import com.openjfx.database.app.component.tabs.TableTab;
import com.openjfx.database.app.model.BaseTabMode;
import com.openjfx.database.common.VertexUtils;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.openjfx.database.app.config.Constants.*;

/**
 * Main interface tabpane
 *
 * @author yangkui
 * @since 1.0
 */
public class MainTabPane extends TabPane {
    /**
     * event-bus address
     */
    public static final String EVENT_BUS_ADDRESS = "controls:mainTabPane";

    {
        registerEventBus();
        getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
    }

    private void registerEventBus() {
        VertexUtils.eventBus().<JsonObject>consumer(EVENT_BUS_ADDRESS, handler -> {
            var body = handler.body();
            var action = body.getString(ACTION);
            var flag = body.getString(FLAG);
            var busAction = EventBusAction.valueOf(action);
            final List<Tab> tabs = new ArrayList<>();
            //Move out a tab
            if (busAction == EventBusAction.REMOVE) {
                getTabs().stream().filter(it -> ((TableTab) it).getModel().getFlag().equals(flag)).findAny()
                        .ifPresent(tabs::add);
            }
            //Move out multiple tabs
            if (busAction == EventBusAction.REMOVE_MANY) {
                var tt = getTabs()
                        .stream().filter(it -> ((BaseTab) it).getModel().getFlag().startsWith(flag))
                        .collect(Collectors.toList());
                tabs.addAll(tt);
            }
            //Clear tab
            if (busAction == EventBusAction.CLEAR) {
                tabs.addAll(getTabs());
            }
            if (!tabs.isEmpty()) {
                Platform.runLater(() -> getTabs().removeAll(tabs));
            }
        });
    }

    /**
     * Message type
     */
    public enum EventBusAction {
        /**
         * Move out a single tab
         */
        REMOVE,
        /**
         * Move out of specified tab
         */
        REMOVE_MANY,
        /**
         * Move out all tabs
         */
        CLEAR
    }
}

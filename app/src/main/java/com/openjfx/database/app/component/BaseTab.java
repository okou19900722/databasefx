package com.openjfx.database.app.component;

import com.openjfx.database.app.model.BaseTabMode;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;

/**
 * base tab
 *
 * @author yangkui
 * @since 1.0
 */
public class BaseTab<T extends BaseTabMode> extends Tab {

    /**
     * Loading state, prevent repeated loading true means false is not in loading
     */
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    /**
     * loading status show
     */
    private final ProgressIndicator progressBar = new ProgressIndicator();

    protected T model;

    public BaseTab(T model) {
        this.model = model;
        //listener current tab loading status
        loading.addListener(((observable, oldValue, newValue) -> {
            final ProgressIndicator indicator;
            if (newValue) {
                indicator = progressBar;
            } else {
                indicator = null;
            }
            Platform.runLater(() -> setGraphic(indicator));
        }));
    }

    public boolean isLoading() {
        return loading.get();
    }

    public BooleanProperty loadingProperty() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading.set(loading);
    }

    public T getModel() {
        return model;
    }
}

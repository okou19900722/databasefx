package com.openjfx.database.app.component;

import com.openjfx.database.app.model.BaseTabMode;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

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

    private ImageView tabIcon;

    protected T model;

    public BaseTab(T model) {
        this.model = model;
        //listener current tab loading status
        loading.addListener(((observable, oldValue, newValue) -> {
            final Node indicator;
            if (newValue) {
                indicator = progressBar;
            } else {
                indicator = tabIcon;
            }
            Platform.runLater(() -> setGraphic(indicator));
        }));
    }

    /**
     * Set the tab icon dynamically, which will be displayed when loading is completed / fails
     *
     * @param image {@link Image}
     */
    protected void setTabIcon(final Image image) {
        if (image != null) {
            tabIcon = new ImageView(image);
        } else {
            tabIcon = null;
        }
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

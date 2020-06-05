package com.openjfx.database.app.component;

import com.openjfx.database.app.model.tab.BaseTabMode;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.openjfx.database.app.DatabaseFX.I18N;

/**
 * base tab
 *
 * @author yangkui
 * @since 1.0
 */
public abstract class BaseTab<T extends BaseTabMode> extends Tab implements Initializable {

    /**
     * Loading state, prevent repeated loading true means false is not in loading
     */
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    /**
     * loading status show
     */
    private final ProgressIndicator progressBar = new ProgressIndicator();
    /**
     * view base path
     */
    private final String VIEW_BASE_PATH = "fxml/component/";
    /**
     * resource bundle
     */
    private ResourceBundle resourceBundle = I18N;

    private ImageView tabIcon;

    protected T model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
    }

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
        setGraphic(tabIcon);
    }

//    /**
//     * When there are multiple database tabs, you can rewrite the logic to distinguish the older database
//     *
//     * @param t Whether there are multiple database tabs
//     */
//    public abstract void updateValue(boolean t);

    /**
     * Called on subclass initialization
     */
    public abstract void init();

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

    protected void loadView(final String view) {
        var path = VIEW_BASE_PATH + view;
        try {
            var url = ClassLoader.getSystemResource(path);
            var fxml = new FXMLLoader(url);
            fxml.setResources(I18N);
            fxml.setController(this);
            var parent = (Node) fxml.load();
            setContent(parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

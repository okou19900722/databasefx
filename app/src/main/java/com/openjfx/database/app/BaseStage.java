package com.openjfx.database.app;

import com.openjfx.database.app.annotation.Layout;
import com.openjfx.database.app.utils.AssetUtils;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.utils.StringUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;


/**
 * base stage
 *
 * @param <D> Pass parameter type
 * @author yangkui
 * @since 1.0
 */
public class BaseStage<D> extends Stage {

    protected Scene scene = null;

    protected BaseController<D> controller;

    /**
     * No parameters need to be passed
     */
    public BaseStage() {
        initController();
        controller.init();
        initStage();
    }

    /**
     * Parameters need to be passed
     *
     * @param data param
     */

    public BaseStage(D data) {
        initController();
        controller.setData(data);
        controller.init();
        initStage();
    }

    /**
     * Get layout annotation
     *
     * @return Return annotation information
     */
    private Layout getLayout() {
        Layout layout = this.getClass().getAnnotation(Layout.class);
        if (Objects.isNull(layout)) {
            throw new RuntimeException(DatabaseFX.I18N.getString("base.stage.layout.null"));
        }
        return layout;
    }

    /**
     * init controller
     */
    private void initController() {
        var layout = getLayout();

        var path = "fxml/" + layout.layout();
        var url = ClassLoader.getSystemResource(path);
        var loader = new FXMLLoader(url);
        loader.setResources(DatabaseFX.I18N);
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            DialogUtils.showErrorDialog(e, DatabaseFX.I18N.getString("base.stage.layout.load.fail"));
            throw new RuntimeException(e);
        }
        scene = new Scene(root);
        //add global style
        scene.getStylesheets().add("css/base.css");
        controller = loader.getController();
        controller.setStage(this);
    }

    /**
     * init stage
     */
    private void initStage() {
        Layout layout = getLayout();

        setScene(scene);
        setWidth(layout.width());
        setHeight(layout.height());
        setMaximized(layout.maximized());
        setResizable(layout.resizable());
        if (StringUtils.isEmpty(getTitle())) {
            var title = StringUtils.isEmpty(layout.title()) ? "" : DatabaseFX.I18N.getString(layout.title());
            setTitle(title);
        }
        setAlwaysOnTop(layout.alwaysOnTop());

        Image icon = getLocalImage(200, 200, layout.icon());

        initStyle(layout.stageStyle());
        setIconified(layout.iconified());

        getIcons().add(icon);
        initModality(layout.modality());
        if (!layout.show()) {
            return;
        }
        if (layout.await()) {
            showAndWait();
        } else {
            show();
        }
    }
}

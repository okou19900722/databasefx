package com.openjfx.database.app.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import static com.openjfx.database.app.utils.AssetUtils.getCssStyle;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

public class SearchPopup extends Popup {

    private static final double ICON_WIDTH = 0x14;
    private static final double ICON_HEIGHT = 0x14;

    private final static Image UP_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "up_icon.png");
    private final static Image DOWN_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "down_icon.png");
    private final static Image CLOSE_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "close.png");

    private final JFXTextField textField = new JFXTextField();


    public SearchPopup() {
        var hBox = new HBox();
        var label = new Label("0 result");
        var up = new JFXButton();
        var down = new JFXButton();
        var close = new JFXButton();

        close.setGraphic(new ImageView(CLOSE_ICON));
        up.setGraphic(new ImageView(UP_ICON));
        down.setGraphic(new ImageView(DOWN_ICON));

        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(textField, label, up, down, close);

        hBox.getStyleClass().add("search-popup");
        getContent().add(hBox);
        setAutoFix(true);
        setAutoHide(true);

        hBox.getStylesheets().add(getCssStyle("search_popup.css"));

        close.setOnAction(e -> hide());

    }

    public void textChange(final ChangeListener<String> handler) {
        textField.textProperty().addListener(handler);
    }

    /**
     * default show strategy
     */
    public void defaultShowStrategy(final Window window) {
        var x = window.getX();
        var y = window.getY();
        var w = window.getWidth();
        var xp = x + w - 20;
        var yp = y + 70;
        show(window, xp, yp);
    }
}

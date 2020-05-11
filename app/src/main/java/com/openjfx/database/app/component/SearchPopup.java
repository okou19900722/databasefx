package com.openjfx.database.app.component;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.openjfx.database.common.Handler;
import javafx.beans.property.SimpleIntegerProperty;
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
    private final Label label = new Label("0 result");

    private final SimpleIntegerProperty indexProperty = new SimpleIntegerProperty(-1);

    private int searchMax = 0;


    public SearchPopup() {
        var hBox = new HBox();

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

        up.setOnAction(e -> {
            var index = getIndexProperty();
            if (index >= 1) {
                setIndexProperty(--index);
            }
        });
        down.setOnAction(e -> {
            var index = getIndexProperty();
            if (index < searchMax - 1) {
                setIndexProperty(++index);
            }
        });

    }

    /**
     * register input text change callback.
     *
     * @param handler callback handler
     */
    public void textChange(final Handler<Integer, String> handler) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (getIndexProperty() != -1) {
                setIndexProperty(-1);
            }
            if (newValue != null && newValue.length() > 0) {
                var number = handler.handler(newValue);
                searchMax = number;
                label.setText(number + "条结果");
            } else {
                label.setText("0条结果");
            }
        });
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

    public int getIndexProperty() {
        return indexProperty.get();
    }

    public SimpleIntegerProperty indexPropertyProperty() {
        return indexProperty;
    }

    public void setIndexProperty(int indexProperty) {
        this.indexProperty.set(indexProperty);
    }
}

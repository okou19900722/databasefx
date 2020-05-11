package com.openjfx.database.app.component;

import com.jfoenix.controls.JFXButton;
import com.openjfx.database.common.Handler;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import static com.openjfx.database.app.utils.AssetUtils.getCssStyle;
import static com.openjfx.database.app.utils.AssetUtils.getLocalImage;

/**
 * <p>This component provides basic search function, search completion keyword callback and up and down coefficient callback</p>
 *
 * @author yangkui
 * @since 1.0
 */
public class SearchPopup extends HBox {

    private static final double ICON_WIDTH = 0x14;
    private static final double ICON_HEIGHT = 0x14;

    private final static Image UP_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "up_icon.png");
    private final static Image DOWN_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "down_icon.png");
    private final static Image CLOSE_ICON = getLocalImage(ICON_WIDTH, ICON_HEIGHT, "close.png");

    private final TextField textField = new TextField();
    private final Label label = new Label("0 result");

    private final SimpleIntegerProperty indexProperty = new SimpleIntegerProperty(-1);

    private int searchMax = 0;


    public SearchPopup() {
        var up = new JFXButton();
        var down = new JFXButton();
        var close = new JFXButton();
        var lBox = new HBox();
        var rBox = new HBox();

        close.setGraphic(new ImageView(CLOSE_ICON));
        up.setGraphic(new ImageView(UP_ICON));
        down.setGraphic(new ImageView(DOWN_ICON));

        setAlignment(Pos.CENTER);
        lBox.getChildren().addAll(textField, label, up, down);
        rBox.getChildren().addAll(close);

        HBox.setHgrow(textField, Priority.ALWAYS);

        lBox.prefWidthProperty().bind(widthProperty().multiply(0.7));
        rBox.prefWidthProperty().bind(widthProperty().multiply(0.3));

        lBox.getStyleClass().add("left-box");
        rBox.getStyleClass().add("right-box");

        getChildren().addAll(lBox, rBox);

        getStyleClass().add("search-popup");

        getStylesheets().add(getCssStyle("search_popup.css"));

        close.setOnAction(e -> {
            var borderPane = (BorderPane) getParent();
            borderPane.setTop(null);
        });

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
        textField.setOnKeyPressed(e -> {
            //hidden search box
            if (e.getCode() == KeyCode.ESCAPE) {
                close.fire();
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

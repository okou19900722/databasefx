package com.openjfx.database.app.controls;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * work station
 *
 * @author yangkui
 * @since 1.0
 */
public class WorkStation extends VBox {

    /**
     * items
     */
    private final ObservableList<String> items = FXCollections.observableArrayList();
    /**
     * title
     */
    private final StringProperty title = new SimpleStringProperty();
    /**
     * list view
     */
    private final ListView<WorkStationNode> listView = new ListView<>();
    /**
     * hBox
     */
    private final HBox hBox = new HBox();
    /**
     * label
     */
    private final Label label = new Label();

    private final StringProperty titleProperty = label.textProperty();

    public WorkStation(final String title) {
        label.setText(title);
        hBox.getChildren().add(label);
        getChildren().addAll(hBox, listView);
        label.getStyleClass().add("title");
        hBox.getStyleClass().add("title-box");
        getStyleClass().add("work-station");
        getStylesheets().add("css/workstation_style.css");


        items.addListener((ListChangeListener<String>) c -> {
            while (c.next()) {
                //add
                for (String s : c.getAddedSubList()) {
                    var node = new WorkStationNode(s);
                    listView.getItems().add(node);
                }
                //remove
                for (String s : c.getRemoved()) {
                    var option = listView.getItems().stream()
                            .filter(node -> node.label.getText().equals(s)).findAny();
                    option.ifPresent((node) -> listView.getItems().remove(node));
                }
            }
        });
        hBox.addEventFilter(MouseEvent.ANY, new MouseEventListener());
    }

    public ObservableList<String> getItems() {
        return items;
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    /**
     * private help class
     */
    private static class WorkStationNode extends HBox {
        /**
         * select model
         */
        private final CheckBox checkBox = new CheckBox();
        /**
         * label
         */
        private final Label label = new Label();

        public WorkStationNode(String text) {
            label.setText(text);
            var hBox = new HBox();
            hBox.getChildren().add(label);
            HBox.setHgrow(hBox, Priority.ALWAYS);
            getChildren().addAll(checkBox, hBox);
            getStyleClass().add("work-station-node");
        }
    }

    public String getTitleProperty() {
        return titleProperty.get();
    }

    public StringProperty titlePropertyProperty() {
        return titleProperty;
    }

    public void setTitleProperty(String titleProperty) {
        this.titleProperty.set(titleProperty);
    }

    /**
     * listener mouse event
     */
    private class MouseEventListener implements EventHandler<MouseEvent> {
        /**
         * x and y offset
         */
        private double xOffset = 0;
        private double yOffset = 0;

        @Override
        public void handle(MouseEvent event) {
            var type = event.getEventType();
            if (type == MouseEvent.MOUSE_PRESSED) {
                xOffset = event.getSceneX();
                yOffset = event.getScreenY();
            }
            if (type == MouseEvent.MOUSE_DRAGGED) {
                var that = WorkStation.this;
                if (event.getSceneX() <= 0) {
                    that.setLayoutX(0);
                } else {
                    that.setLayoutX(event.getSceneX());
                }
                if (event.getScreenY() <= 0) {
                    that.setLayoutY(0);
                } else {
                    that.setLayoutY(event.getSceneY());
                }
            }
        }
    }
}

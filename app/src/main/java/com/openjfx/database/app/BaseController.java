package com.openjfx.database.app;

import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * base controller
 *
 * @param <D> Transfer data type
 * @author yangkui
 * @since 1.0
 */
public abstract class BaseController<D> implements Initializable {
    /**
     * extension data
     */
    protected D data;
    /**
     * stage reference
     */
    protected Stage stage;
    /**
     * ResourceBundle
     */
    protected ResourceBundle resourceBundle;
    /**
     * URL
     */
    protected URL location;

    /**
     * Called when initializing the fxml view
     * {@inheritDoc}
     *
     * @param location  location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.resourceBundle = resources;
        this.location = location;
    }

    /**
     * init controller
     */
    public void init() {
        //todo override
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

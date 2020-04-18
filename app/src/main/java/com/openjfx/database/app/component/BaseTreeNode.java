package com.openjfx.database.app.component;


import com.openjfx.database.app.utils.DialogUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据库节点base类
 *
 * @param <T> value type
 */
public abstract class BaseTreeNode<T> extends TreeItem<T> {

    /**
     * 加载状态,防止重复加载 true表示加载中 false表示不再
     */
    private final BooleanProperty loading = new SimpleBooleanProperty();

    /**
     * uuid 数据库标识
     */
    protected String uuid;

    /**
     * 初始化子节点时调用
     */
    public abstract void init();

    /**
     * 菜单列表
     */
    protected List<MenuItem> menus = new ArrayList<>();

    /**
     * 加载进度信息
     */
    private final ProgressIndicator indicator = new ProgressIndicator();

    /**
     * 节点构造器
     *
     * @param uuid  数据库标识
     * @param image 节点图标
     */
    public BaseTreeNode(String uuid, Image image) {
        this.uuid = uuid;
        var icon = new ImageView(image);
        var stackPane = new StackPane();

        indicator.setPrefWidth(20);
        indicator.setPrefHeight(20);
        indicator.setVisible(false);

        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().addAll(indicator, icon);
        setGraphic(stackPane);
        //检测状态变化
        loading.addListener((observable, oldValue, newValue) ->
                Platform.runLater(() ->
                        {
                            if (newValue) {
                                icon.setVisible(false);
                                indicator.setVisible(true);
                            } else {
                                indicator.setVisible(false);
                                icon.setVisible(true);
                            }
                        }
                ));
    }

    /**
     * 初始化失败
     * @param throwable 异常信息
     * @param message 错误提示信息
     */
    protected void initFailed(Throwable throwable,String message){
        DialogUtils.showErrorDialog(throwable, message);
        setLoading(false);
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * 刷新
     */
    public void flush() {
        Platform.runLater(()->{
            setExpanded(false);
            getChildren().clear();
            init();
        });
    }

    public List<MenuItem> getMenus() {
        return menus;
    }

    protected void addMenus(MenuItem... menuItems) {
        this.menus.addAll(Arrays.asList(menuItems));
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
}

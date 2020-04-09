package com.openjfx.database.app.component;


import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

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
     * 加载状态,防止重复加载
     */
    protected boolean loading = false;

    /**
     * uuid
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

    public BaseTreeNode(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    /**
     * 刷新
     */
    public void flush() {
        Platform.runLater(() -> {
            loading = false;
            init();
        });
    }

    public List<MenuItem> getMenus() {
        return menus;
    }

    protected void addMenus(MenuItem... menuItems) {
        this.menus.addAll(Arrays.asList(menuItems));
    }
}

package com.openjfx.database.app.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 *
 *
 * alert工具类
 *
 * @author yangkui
 * @since 1.0
 *
 */
public class AlertUtils {
    /**
     * 显示确定对话框
     * @param content 对话框内容
     * @return 返回用户确定类型
     */
    public static Optional<ButtonType> showConfirm(String content){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}

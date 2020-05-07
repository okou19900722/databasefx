package com.openjfx.database.app.utils;

import com.openjfx.database.app.enums.NotificationType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ExceptionDialog;

import java.util.Optional;

/**
 * 显示对话框
 *
 * @author yangkui
 * @since 1.0
 */
public class DialogUtils {
    /**
     * 显示错误对话框
     *
     * @param title     对话框顶部信息
     * @param throwable 错误信息
     */
    public static void showErrorDialog(Throwable throwable, String title) {

        Platform.runLater(() -> {
            ExceptionDialog dialog = new ExceptionDialog(throwable);
            dialog.setTitle(title);
            dialog.setHeaderText("异常堆栈信息：");
            dialog.setResizable(false);
            dialog.getDialogPane().getStylesheets().add(AssetUtils.BASE_STYLE);
            dialog.show();
        });
    }

    /**
     * 显示通知
     *
     * @param text 通知内容
     * @param pos  通知显示位置
     * @param type 通知类型
     */
    public static void showNotification(String text, Pos pos, NotificationType type) {
        Platform.runLater(() -> {
            Notifications notifications = Notifications.create();
            notifications.position(pos);
            notifications.text(text);
            switch (type) {
                case ERROR:
                    notifications.showError();
                    break;
                case WARNING:
                    notifications.showWarning();
                    break;
                case INFORMATION:
                    notifications.showInformation();
                    break;
                case CONFIRMATION:
                    notifications.showConfirm();
                    break;
                default:
                    notifications.show();
            }
        });
    }

    /**
     * 显示确认对话框
     *
     * @param message 消息内容
     * @return 返回确认结果, 如果点击ok则返回true 否则返回false
     * @apiNote 调用该方法得在Fx ui线程之中
     */
    public static boolean showAlertConfirm(String message) {
        var alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(AssetUtils.BASE_STYLE);
        var optional = alert.showAndWait();
        return optional.isPresent() && optional.get() == ButtonType.OK;
    }

    /**
     * 显示对话框信息
     *
     * @param message 消息内容
     */
    public static void showAlertInfo(String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("消息");
        alert.setContentText(message);
        alert.getDialogPane().getStylesheets().add(AssetUtils.BASE_STYLE);
        alert.showAndWait();
    }
}

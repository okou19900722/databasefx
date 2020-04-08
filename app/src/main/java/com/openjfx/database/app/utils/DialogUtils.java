package com.openjfx.database.app.utils;

import com.openjfx.database.app.enums.NotificationType;
import javafx.application.Platform;
import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;
import org.controlsfx.dialog.ExceptionDialog;

/**
 *
 *
 * 显示对话框
 *
 * @author yangkui
 * @since 1.0
 *
 */
public class DialogUtils {
    /**
     * 显示错误对话框
     *
     * @param title 对话框顶部信息
     * @param throwable 错误信息
     */
    public static void showErrorDialog(Throwable throwable,String title){
        Platform.runLater(()->{
            ExceptionDialog dialog = new ExceptionDialog(throwable);
            dialog.setTitle(title);
            dialog.setHeaderText("异常堆栈信息：");
            dialog.setResizable(false);
            dialog.show();
        });
    }

    /**
     * 显示通知
     * @param text 通知内容
     * @param pos 通知显示位置
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
}

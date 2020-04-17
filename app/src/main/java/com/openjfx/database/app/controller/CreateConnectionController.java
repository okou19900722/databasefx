package com.openjfx.database.app.controller;

import com.openjfx.database.app.config.DbPreference;
import com.openjfx.database.app.enums.NotificationType;
import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.utils.DialogUtils;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.model.ConnectionParam;
import com.openjfx.database.mysql.MysqlHelper;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.Objects;
import java.util.Optional;

import static com.openjfx.database.app.config.Constants.ACTION;
import static com.openjfx.database.app.config.Constants.UUID;
import static com.openjfx.database.app.config.DbPreference.updateConnection;
import static com.openjfx.database.app.config.FileConfig.saveConnection;
import static com.openjfx.database.app.utils.DialogUtils.showErrorDialog;
import static com.openjfx.database.app.utils.DialogUtils.showNotification;
import static com.openjfx.database.common.utils.StringUtils.isEmpty;

/**
 * 创建连接控制器
 *
 * @author yangkui
 * @since 1.0
 */
public class CreateConnectionController extends BaseController<String> {
    @FXML
    private TextField server;
    @FXML
    private TextField name;
    @FXML
    private TextField port;
    @FXML
    private TextField user;
    @FXML
    private PasswordField password;

    @Override
    public void init() {
        if (data != null) {
            //加载配置信息
            DbPreference.getConnectionParam(data).ifPresent(cc -> {
                server.setText(cc.getHost());
                name.setText(cc.getName());
                port.setText(String.valueOf(cc.getPort()));
                user.setText(cc.getUser());
                password.setText(cc.getPassword());
            });
        }
    }

    @FXML
    private void test() {
        ConnectionParam param = validatorParam();
        if (Objects.isNull(param)) {
            return;
        }
        Future<Boolean> future = MysqlHelper.testConnection(param);
        future.onSuccess(r -> showNotification("连接成功", Pos.TOP_CENTER, NotificationType.INFORMATION));
        future.onFailure(t -> showErrorDialog(t, "连接失败"));
    }

    @FXML
    private void close() {
        stage.close();
    }

    @FXML
    private void save() {
        ConnectionParam param = validatorParam();
        if (param == null) {
            return;
        }
        boolean flag = true;
        if (Objects.nonNull(data)) {
            //更新连接
            updateConnection(param);
            flag = DialogUtils.showAlertConfirm("连接已更改是否重连?");
        } else {
            //新建连接
            saveConnection(param);
            DbPreference.addConnection(param);
        }
        JsonObject message = new JsonObject();
        DatabaseFxController.EventBusAction action = Objects.nonNull(data)
                ? DatabaseFxController.EventBusAction.UPDATE_CONNECTION
                : DatabaseFxController.EventBusAction.ADD_CONNECTION;

        message.put(ACTION, action);
        message.put(UUID, param.getUuid());
        boolean a = Objects.isNull(data) && flag;

        if (!flag || a) {
            VertexUtils.eventBus().send(DatabaseFxController.EVENT_ADDRESS, message);
        }

        stage.close();
    }

    private ConnectionParam validatorParam() {
        String host = server.getText();
        String p = port.getText();
        String u = user.getText();
        String ps = password.getText();
        String n = name.getText();

        if (isEmpty(host) || isEmpty(p) || isEmpty(u) || isEmpty(n)) {
            showNotification("参数不全", Pos.TOP_CENTER, NotificationType.WARNING);
            return null;
        }

        ConnectionParam param = new ConnectionParam();

        if (Objects.nonNull(data)) {
            param.setUuid(data);
        }

        param.setHost(host);
        param.setUser(u);
        param.setPassword(ps);
        param.setPort(Integer.parseInt(p));
        param.setName(n);

        return param;
    }
}

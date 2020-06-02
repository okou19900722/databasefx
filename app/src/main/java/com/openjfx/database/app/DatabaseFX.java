package com.openjfx.database.app;

import com.openjfx.database.base.AbstractDatabaseSource;
import com.openjfx.database.app.stage.SplashStage;
import com.openjfx.database.common.VertexUtils;
import com.openjfx.database.mysql.MySql;
import javafx.application.Application;

import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * JavaFX program entry
 *
 * @author yangkui
 * @since 1.0
 */
public class DatabaseFX extends Application {

    public final static AbstractDatabaseSource DATABASE_SOURCE = new MySql();

    public final static ResourceBundle I18N = ResourceBundle.getBundle("i18n/databasefx");

    @Override
    public void start(Stage stage) {
        new SplashStage();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        DATABASE_SOURCE.closeAll();
        VertexUtils.close();
    }
}

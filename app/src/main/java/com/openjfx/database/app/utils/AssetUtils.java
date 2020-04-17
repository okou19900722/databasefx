package com.openjfx.database.app.utils;

import com.openjfx.database.common.VertexUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.io.InputStream;

import static com.openjfx.database.app.utils.DialogUtils.showErrorDialog;

/**
 * 资源操作工具类
 *
 * @author yangkui
 * @since 1.0
 */
public class AssetUtils {
    /**
     * css根路径
     */
    private static final String CSS_PATH = "css/";

    /**
     * fxml文件根路径
     */
    private static final String FXML_PATH = "fxml/";
    /**
     * 图片根路径
     */
    private static final String IMAGE_PATH = "assets/images/";
    /**
     * 字体路径
     */
    private static final String FONT_PATH = "fonts/";
    /**
     * 字体默认尺寸
     */
    private static final double DEFAULT_FONT_SIZE = 12D;
    /**
     * 字体列表
     */
    private static final String[] FONTS = new String[]{
            FONT_PATH + "SourceHanSerifCN-Light.otf"
    };

    /**
     * 基本样式
     */
    public static final String BASE_STYLE = AssetUtils.getCssStyle("base.css");

    /**
     * 加载css样式
     *
     * @param name css文件名
     * @return 返回css路径
     */
    public static String getCssStyle(String name) {
        String path = CSS_PATH + name;
        return ClassLoader.getSystemResource(path).toExternalForm();
    }

    /**
     * 加载fxml视图
     *
     * @param fileName 文件名
     * @return 返回视图信息
     */
    public static Parent getFxml(String fileName) {
        String path = FXML_PATH + fileName;
        Parent root;

        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource(path));
        } catch (IOException e) {
            showErrorDialog(e, "加载视图失败");
            root = new BorderPane();
        }

        return root;
    }

    /**
     * 加载图片资源
     *
     * @param width    图片宽度
     * @param height   图片高度
     * @param filename 图片名称
     * @return 返回Image
     */
    public static Image getLocalImage(double width, double height, String filename) {
        String path = IMAGE_PATH + filename;
        InputStream in = ClassLoader.getSystemResourceAsStream(path);
        return new Image(in, width, height, false, true);
    }

    /**
     * 加载所有app字体
     */
    public static void loadAllFont() {
        for (String font : FONTS) {
            var in = ClassLoader.getSystemResourceAsStream(font);
            Font.loadFont(in, DEFAULT_FONT_SIZE);
        }
    }
}

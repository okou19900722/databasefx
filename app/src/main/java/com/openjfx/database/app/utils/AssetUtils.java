package com.openjfx.database.app.utils;

import javafx.scene.image.Image;
import javafx.scene.text.Font;

import java.io.*;
import java.net.JarURLConnection;
import java.util.Objects;
import java.util.jar.JarEntry;

/**
 * Resource operation utils collection
 *
 * @author yangkui
 * @since 1.0
 */
public class AssetUtils {
    /**
     * Picture root path
     */
    private static final String IMAGE_PATH = "assets/images/";
    /**
     * Font root path
     */
    private static final String FONT_PATH = "fonts/";
    /**
     * Font default size
     */
    private static final double DEFAULT_FONT_SIZE = 12D;
    /**
     * Font list
     */
    private static final String[] FONTS = new String[]{
            FONT_PATH + "SourceHanSerifCN-Light.otf"
    };

    /**
     * load image resource
     *
     * @param width    width of picture
     * @param height   height of picture
     * @param filename name of picture
     * @return return {@link Image} instance
     */
    public static Image getLocalImage(double width, double height, String filename) {
        var path = IMAGE_PATH + filename;
        var in = ClassLoader.getSystemResourceAsStream(path);
        return new Image(Objects.requireNonNull(in), width, height, false, true);
    }

    /**
     * load all font of application
     */
    public static void loadAllFont() throws IOException {
        var url = ClassLoader.getSystemResource(FONT_PATH);
        var protocol = url.getProtocol();
        //load filesystem font list
        if ("file".equals(protocol)) {
            var file = new File(url.getFile());
            for (File item : Objects.requireNonNull(file.listFiles())) {
                var in = new FileInputStream(item);
                Font.loadFont(in, DEFAULT_FONT_SIZE);
            }
            return;
        }
        //load jar file font list
        var jarUrl = (JarURLConnection) url.openConnection();
        var jarFile = jarUrl.getJarFile();
        var entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            var entity = entries.nextElement();
            var name = entity.getName();
            if (name.startsWith(FONT_PATH)) {
                var in = ClassLoader.getSystemResourceAsStream(name);
                Font.loadFont(in, DEFAULT_FONT_SIZE);
            }
        }
    }
}

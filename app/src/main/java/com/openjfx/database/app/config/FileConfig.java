package com.openjfx.database.app.config;

import com.openjfx.database.model.ConnectionParam;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.util.Objects;

import static com.openjfx.database.app.config.Constants.*;
import static com.openjfx.database.common.VertexUtils.getFileSystem;
import static com.openjfx.database.common.utils.OSUtils.getUserHome;

/**
 * Profile management
 *
 * @author yangkui
 * @since 1.0
 */
public class FileConfig {
    /**
     * Profile path
     */
    private final static String CONFIG_PATH = getUserHome() + File.separator + CONFIG_FILE_NAME;
    /**
     * Vertx FileSystem
     */
    private final static FileSystem FILE_SYSTEM = getFileSystem();

    /**
     * Check if the profile path exists, if not, create
     */
    private static void checkDir() {
        boolean a = FILE_SYSTEM.existsBlocking(CONFIG_PATH);
        //If directory does not exist - > create directory
        if (!a) {
            FILE_SYSTEM.mkdirBlocking(CONFIG_PATH);
        }
    }

    /**
     * Load UI profile
     *
     * @param fileName Profile name
     * @return Return to UI configuration JSON
     */
    public static JsonObject loadConfig(String fileName) {
        checkDir();
        var path = CONFIG_PATH + File.separator + fileName;
        final JsonObject jsonObject;
        if (FILE_SYSTEM.existsBlocking(path)) {
            var buffer = FILE_SYSTEM.readFileBlocking(path);
            jsonObject = buffer.toJsonObject();
        } else {
            var buffer = FILE_SYSTEM.readFileBlocking("config/" + fileName);
            jsonObject = buffer.toJsonObject();
            FILE_SYSTEM.createFileBlocking(path);
            FILE_SYSTEM.writeFileBlocking(path, buffer);
        }
        return jsonObject;
    }

    /**
     * Persistent connection information
     *
     * @param param Connection information
     */
    public static void saveConnection(ConnectionParam param) {
        var jsonObject = loadConfig(DB_CONFIG_FILE);
        var array = jsonObject.getJsonArray(DATABASE);
        var obj = JsonObject.mapFrom(param);
        array.add(obj);
        writer(jsonObject, DB_CONFIG_FILE);
    }

    /**
     * Delete cache connection information
     *
     * @param uuid uuid
     */
    public static void deleteCon(String uuid) {
        var jsonObject = loadConfig(DB_CONFIG_FILE);
        var connections = jsonObject.getJsonArray(DATABASE);
        JsonObject target = null;
        for (Object connection : connections) {
            JsonObject con = (JsonObject) connection;
            if (con.getString(UUID).equals(uuid)) {
                target = con;
                break;
            }
        }
        if (Objects.nonNull(target)) {
            connections.remove(target);
        }
        writer(jsonObject, DB_CONFIG_FILE);
    }

    /**
     * Update connection information
     *
     * @param param Connection information
     */
    public static void updateConnection(ConnectionParam param) {
        var jsonObject = loadConfig(DB_CONFIG_FILE);
        var connections = jsonObject.getJsonArray(DATABASE);
        int index = 0;
        JsonObject a = null;
        for (var connection : connections) {
            var con = (JsonObject) connection;
            if (con.getString(UUID).equals(param.getUuid())) {
                a = JsonObject.mapFrom(param);
                break;
            }
            index++;
        }
        if (a == null) {
            throw new RuntimeException("更新目标不存在");
        }
        var aa = new JsonArray();
        for (int i = 0; i < connections.size(); i++) {
            if (i != index) {
                aa.add(connections.getValue(i));
            } else {
                aa.add(a);
            }
        }
        jsonObject.put(DATABASE, aa);
        writer(jsonObject, DB_CONFIG_FILE);
    }

    /**
     * write file
     *
     * @param obj      New data
     * @param fileName file name
     */
    private static void writer(JsonObject obj, String fileName) {
        var path = CONFIG_PATH + File.separator + fileName;
        FILE_SYSTEM.writeFileBlocking(path, obj.toBuffer());
    }
}

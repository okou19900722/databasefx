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
 * 配置文件管理
 *
 * @author yangkui
 * @since 1.0
 */
public class FileConfig {
    private static String configDir = getUserHome() + File.separator + CONFIG_FILE_NAME;
    private static FileSystem fs = getFileSystem();

    /**
     * 检查配置文件路径是否存在,不存在则创建
     */
    private static void checkDir() {
        boolean a = fs.existsBlocking(configDir);
        //如果目录不存在->创建目录
        if (!a) {
            fs.mkdirBlocking(configDir);
        }
    }

    /**
     * 加载ui配置文件
     *
     * @param fileName 配置文件名称
     * @return 返回ui配置json
     */
    public static JsonObject loadConfig(String fileName) {
        checkDir();
        String path = configDir + File.separator + fileName;
        JsonObject jsonObject;
        if (fs.existsBlocking(path)) {
            Buffer buffer = fs.readFileBlocking(path);
            jsonObject = buffer.toJsonObject();
        } else {
            Buffer buffer = fs.readFileBlocking("config/" + fileName);
            jsonObject = buffer.toJsonObject();
            fs.createFileBlocking(path);
            fs.writeFileBlocking(path, buffer);
        }
        return jsonObject;
    }

    /**
     * 持久化连接信息
     *
     * @param param 连接信息
     */
    public static void saveConnection(ConnectionParam param) {
        JsonObject jsonObject = loadConfig(DB_CONFIG_FILE);
        JsonArray array = jsonObject.getJsonArray(DATABASE);
        JsonObject obj = JsonObject.mapFrom(param);
        array.add(obj);
        writer(jsonObject, DB_CONFIG_FILE);
    }

    /**
     * 删除缓存连接信息
     * @param uuid uuid
     */
    public static void deleteCon(String uuid){
        JsonObject jsonObject = loadConfig(DB_CONFIG_FILE);
        JsonArray connections = jsonObject.getJsonArray(DATABASE);
        JsonObject target = null;
        for (Object connection : connections) {
            JsonObject con = (JsonObject) connection;
            if (con.getString(UUID).equals(uuid)){
                target = con;
                break;
            }
        }
        if (Objects.nonNull(target)){
            connections.remove(target);
        }
        writer(jsonObject,DB_CONFIG_FILE);
    }

    /**
     * 更新连接信息
     *
     * @param param 连接信息
     */
    public static void updateConnection(ConnectionParam param) {
        JsonObject jsonObject = loadConfig(DB_CONFIG_FILE);
        JsonArray connections = jsonObject.getJsonArray(DATABASE);
        int index = 0;
        JsonObject a = null;
        for (Object connection : connections) {
            JsonObject con = (JsonObject) connection;
            if (con.getString(UUID).equals(param.getUuid())) {
                a = JsonObject.mapFrom(param);
                break;
            }
            index++;
        }
        if (a==null){
            throw new RuntimeException("更新目标不存在");
        }
        JsonArray aa = new JsonArray();
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
     * 写入文件
     *
     * @param obj      新数据
     * @param fileName 文件名
     */
    private static void writer(JsonObject obj, String fileName) {
        String path = configDir + File.separator + fileName;
        fs.writeFileBlocking(path, obj.toBuffer());
    }
}

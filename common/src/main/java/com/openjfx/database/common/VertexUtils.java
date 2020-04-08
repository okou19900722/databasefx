package com.openjfx.database.common;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;

/**
 * vertx toolkit 工具类
 *
 * @author yangkui
 * @since 1.0
 */
public class VertexUtils {
    /**
     *
     * vertx实例对象
     *
     */
    private static Vertx vertx = Vertx.vertx();

    /**
     *
     * vertx 文件系统
     *
     */
    private static FileSystem fileSystem = vertx.fileSystem();


    public static Vertx getVertex() {
        return vertx;
    }

    public static void close(){
        vertx.close();
    }

    public static FileSystem getFileSystem() {
        return fileSystem;
    }

    public static EventBus eventBus(){
        return vertx.eventBus();
    }
}

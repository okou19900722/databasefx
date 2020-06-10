package com.openjfx.database.common;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonObject;

/**
 * vertx toolkit utils
 *
 * @author yangkui
 * @since 1.0
 */
public class VertexUtils {
    /*
     * In this domestic environment, it is necessary to disable the default DNS resolver of vertx
     */
    static {
        System.setProperty("vertx.disableDnsResolver", "true");
    }

    /**
     * Vertx instance object
     */
    private static final Vertx VERTX = Vertx.vertx();

    /**
     * Vertx file system
     */
    private static final FileSystem FILE_SYSTEM = VERTX.fileSystem();


    public static Vertx getVertex() {
        return VERTX;
    }

    public static void close() {
        VERTX.close();
    }

    public static FileSystem getFileSystem() {
        return FILE_SYSTEM;
    }

    public static EventBus eventBus() {
        return VERTX.eventBus();
    }

    /**
     * writer file
     *
     * @param path  file path
     * @param bytes byte
     * @return return writer result
     */
    public static Future<Void> writerFile(String path, byte[] bytes) {
        return FILE_SYSTEM.writeFile(path, Buffer.buffer(bytes));
    }

    /**
     * by event send message
     *
     * @param address Eventbus address;
     * @param message message content
     */
    public static void send(final String address, final JsonObject message) {
        VERTX.eventBus().send(address, message);
    }
}

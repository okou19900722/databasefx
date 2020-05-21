package com.openjfx.database;

import com.openjfx.database.model.DatabaseCharsetModel;

import java.util.List;

/**
 * Database character set operation interface
 *
 * @author yangkui
 * @since 1.0
 */
public interface DataCharset {
    /**
     * according by collation get relation charset
     *
     * @param collation collation
     * @return return charset
     */
    String getCharset(final String collation);

    /**
     * by charset name get relation collation list
     *
     * @param charsetName charset name
     * @return return charset collation
     */
    List<String> getCharsetCollations(final String charsetName);

    /**
     * get current database support charset
     *
     * @return return charset list
     */
    List<String> getCharset();
}

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
     * obtain give charset length
     *
     * @param charset give charset
     * @return return charset length
     */
    int getCharsetLength(String charset);

    /**
     * obtain current database support charset
     *
     * @return return charset list
     */
    List<DatabaseCharsetModel> getDatabaseCharset();

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

    /**
     * Determine whether the current type is a numeric type
     *
     * @param charset target charset
     * @return is number?
     */
    boolean number(final String charset);

    /**
     * Determine whether the current type is string type
     *
     * @param charset target charset
     * @return is string?
     */
    boolean string(final String charset);

    /**
     * Determine whether the current type is time type
     *
     * @param charset target charset
     * @return is time type？
     */
    boolean datetime(final String charset);
}

package com.openjfx.database;

import com.openjfx.database.model.DatabaseCharsetModel;

import java.util.List;

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
}

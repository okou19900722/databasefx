package com.openjfx.database.model;

import java.util.List;

/**
 * database charset
 *
 * @author yangkui
 * @since 1.0
 */
public class DatabaseCharsetModel {
    /**
     * charset
     */
    private String charset;
    /**
     * support collation list
     */
    private List<String> collations;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public List<String> getCollations() {
        return collations;
    }

    public void setCollations(List<String> collations) {
        this.collations = collations;
    }

    @Override
    public String toString() {
        return "DatabaseCharsetModel{" +
                "charset='" + charset + '\'' +
                ", collations=" + collations +
                '}';
    }
}

package com.openjfx.database.app.utils;

import javafx.scene.control.Labeled;
import javafx.scene.text.Font;

/**
 * ui relation
 *
 * @author yangkui
 * @since 1.0
 */
public class UiUtils {
    /**
     * compute text width
     *
     * @param control control node
     * @return text width
     */
    public static <T extends Labeled> double computeTextWidth(T control) {
        var text = control.getText();
        var font = control.getFont();
        var width = computeTextWidth(text, font);
        var padding = control.getPadding();
        var textGap = control.getGraphicTextGap();
        return width + padding.getLeft() + padding.getRight() + textGap;
    }

    /**
     * Calculates the length of the target string based on the number of strings and font pixels
     *
     * @param text target text
     * @param font target font
     * @return target width
     */
    public static double computeTextWidth(final String text, final Font font) {
        var fontSize = font.getSize();
        var length = text.length();
        return fontSize * length;
    }
}

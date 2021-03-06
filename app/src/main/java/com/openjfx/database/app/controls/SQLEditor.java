package com.openjfx.database.app.controls;


import com.openjfx.database.common.VertexUtils;
import javafx.geometry.Point2D;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * sql editor
 *
 * @author yangkui
 * @since 1.0
 */
public class SQLEditor extends CodeArea {
    /**
     * Capitalized keywords
     */
    private static final String[] UPPER_KEYWORD;

    //Dynamically load keyword list
    static {
        var path = "database/keyword.json";
        var fileSystem = VertexUtils.getFileSystem();
        if (!fileSystem.existsBlocking(path)) {
            UPPER_KEYWORD = new String[0];
        } else {
            var buffer = fileSystem.readFileBlocking(path);
            var array = buffer.toJsonArray();
            UPPER_KEYWORD = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                var keyword = array.getString(i);
                UPPER_KEYWORD[i] = keyword.toUpperCase();
            }
        }
    }

    /**
     * Lowercase keyword
     */
    private static final String[] LOW_KEYWORD = Arrays.stream(UPPER_KEYWORD).map(String::toLowerCase).toArray(String[]::new);

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", UPPER_KEYWORD) + "|" + String.join("|", LOW_KEYWORD) + ")\\b";
    private static final String PAREN_PATTERN = "[()]";
    private static final String BRACE_PATTERN = "[{}]";
    private static final String BRACKET_PATTERN = "[\\[\\]]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")");

    public SQLEditor() {
        setWrapText(true);

        setParagraphGraphicFactory(LineNumberFactory.get(this));

        this.multiPlainChanges().successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> this.setStyleSpans(0, computeHighlighting(this.getText())));

        final var whiteSpace = Pattern.compile("^\\s+");
        addEventFilter(KeyEvent.KEY_PRESSED, kE -> {
            if (kE.getCode() == KeyCode.ENTER) {
                int caretPosition = getCaretPosition();
                int currentParagraph = getCurrentParagraph();
                Matcher m0 = whiteSpace.matcher(getParagraph(currentParagraph - 1).getSegments().get(0));
                insertText(caretPosition, m0.group());
            }
        });
        setInputMethodRequests(new InputMethodRequestsObject());
        setOnInputMethodTextChanged(event -> {
            if (!"".equals(event.getCommitted())) {
                insertText(getCaretPosition(), event.getCommitted());
            }
        });
        var form = ClassLoader.getSystemResource("css/sql_edit.css").toExternalForm();
        getStylesheets().add(form);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        var matcher = PATTERN.matcher(text);
        var lastKwEnd = 0;
        var spansBuilder = new StyleSpansBuilder<Collection<String>>();
        while (matcher.find()) {
            var styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public void setText(final String text) {
        clear();
        insertText(0, text);
    }

    static class InputMethodRequestsObject implements InputMethodRequests {

        @Override
        public Point2D getTextLocation(int offset) {
            return new Point2D(0, 0);
        }

        @Override
        public int getLocationOffset(int x, int y) {
            return 0;
        }

        @Override
        public void cancelLatestCommittedText() {

        }

        @Override
        public String getSelectedText() {
            return "";
        }
    }
}

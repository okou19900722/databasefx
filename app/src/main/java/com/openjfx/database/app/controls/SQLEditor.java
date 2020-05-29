package com.openjfx.database.app.controls;


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

public class SQLEditor extends CodeArea {
    /**
     * 大写关键字
     */
    private static final String[] UPPER_KEYWORD = new String[]{
            "ADD",
            "ALL",
            "ALTER",
            "ANALYZE",
            "AND",
            "AS",
            "ASC",
            "ASENSITIVE",
            "BEFORE",
            "BETWEEN",
            "BIGINT",
            "BINARY",
            "SELECT",
            "INSERT",
            "UPDATE",
            "DELETE",
            "SHOW",
            "DROP",
            "WHERE",
            "FROM",
            "LIMIT",
            "INNER",
            "LEFT",
            "RIGHT",
            "LIMIT",
            "CREATE",
            "DATABASE",
            "CHARACTER",
            "SET",
            "COLLATE"
    };
    /**
     * 小写关键字
     */
    private static final String[] LOW_KEYWORD = Arrays.stream(UPPER_KEYWORD).map(String::toLowerCase).toArray(String[]::new);

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", UPPER_KEYWORD) + String.join("|", LOW_KEYWORD) + ")\\b";
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
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
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
}

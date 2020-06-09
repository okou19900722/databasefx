package com.openjfx.database.app.component.paginations;

import com.openjfx.database.app.model.ExportWizardModel;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

/**
 * export wizard info page
 *
 * @author yangkui
 * @since 1.0
 */
public class ExportWizardInfoPage extends BorderPane {
    /**
     * export text info
     */
    private final TextArea textArea = new TextArea();
    /**
     * export progress info
     */
    private final ProgressBar progressBar = new ProgressBar();

    public ExportWizardInfoPage(ExportWizardModel model) {
        textArea.setEditable(false);
        progressBar.setProgress(0);
        setCenter(textArea);
        setBottom(progressBar);
        getStyleClass().add("export-wizard-info");
        progressBar.prefWidthProperty().bind(widthProperty());
    }

    public void appendStr(String str) {
        var ss = str + "\r\n";
        Platform.runLater(() -> textArea.appendText(ss));
    }

    public void updateProgressValue(double value) {
        Platform.runLater(() -> progressBar.setProgress(value));
    }

    /**
     * reset progress info
     */
    public void reset() {
        textArea.clear();
        progressBar.setProgress(0);
    }
}

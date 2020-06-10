package com.openjfx.database.app.component.paginations;

import com.openjfx.database.app.controls.SQLEditor;
import com.openjfx.database.app.model.ExportWizardModel;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
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
    private final SQLEditor textArea = new SQLEditor();
    /**
     * export progress info
     */
    private final ProgressBar progressBar = new ProgressBar();

    public ExportWizardInfoPage(ExportWizardModel model) {
        textArea.setEditable(false);
        progressBar.setProgress(0);
        textArea.setWrapText(true);
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

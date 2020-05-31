package com.openjfx.database.app.skin;

import com.openjfx.database.app.controls.DataLabel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * DataLabel skin
 *
 * <p>{@link com.openjfx.database.app.controls.DataLabel}</p>
 *
 * @author yangkui
 * @since 1.0
 */
public class DataLabelSkin extends SkinBase<DataLabel> {

    public DataLabelSkin(DataLabel control) {
        super(control);
        // If the text exceeds the cell range, the extension button will be displayed, and a window will pop up to display all the contents
        final var extendData = new Button("扩展");
        //Used to display text messages
        final var text = new Text(control.getText());
        final var hBox = new HBox();
        hBox.getChildren().addAll(text, extendData);
        getChildren().add(hBox);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

    }

    @Override
    protected void layoutInArea(Node child, double areaX, double areaY,
                                double areaWidth, double areaHeight,
                                double areaBaselineOffset,
                                HPos halignment, VPos valignment) {
        layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                Insets.EMPTY, true, true, halignment, valignment);
    }
}

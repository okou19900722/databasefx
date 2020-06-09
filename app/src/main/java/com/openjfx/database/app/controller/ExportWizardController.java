package com.openjfx.database.app.controller;

import com.openjfx.database.app.BaseController;
import com.openjfx.database.app.component.paginations.ExportWizardFormatPage;
import com.openjfx.database.app.component.paginations.ExportWizardInfoPage;
import com.openjfx.database.app.component.paginations.ExportWizardSelectColumnPage;
import com.openjfx.database.app.model.ExportWizardModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

/**
 * Export Wizard controller
 *
 * @author yangkui
 * @since 1.0
 */
public class ExportWizardController extends BaseController<ExportWizardModel> {

    @FXML
    private Label wizardTitle;

    @FXML
    private Pagination pagination;

    @FXML
    private Button startOrCancel;

    private static final String[] TITLE = {
            "向导可以让你指定导出数据的细节。你要使用哪一种导出格式?(1/3)",
            "你可以选择导出哪些列或者定制化导出列。(2/3)",
            "我们已收集向导导出数据所需要的全部信息。点击[开始]按钮开始导出。(3/3)"
    };
    /**
     * format page
     */
    private ExportWizardFormatPage formatPage;
    /**
     * select column page
     */
    private ExportWizardSelectColumnPage selectColumnPage;

    private ExportWizardInfoPage infoPage;

    @Override
    public void init() {

        formatPage = new ExportWizardFormatPage(data);
        selectColumnPage = new ExportWizardSelectColumnPage(data);
        infoPage = new ExportWizardInfoPage(data);
        pagination.setPageFactory(pageFactory());
    }

    public Callback<Integer, Node> pageFactory() {
        return (index) -> {
            final Node node;
            if (index == 0) {
                node = formatPage;
            } else if (index == 1) {
                node = selectColumnPage;
            } else {
                node = infoPage;
            }
            wizardTitle.setText(TITLE[index]);
            return node;
        };
    }

    @FXML
    public void next(ActionEvent event) {
        var index = pagination.getCurrentPageIndex();
        if (index < pagination.getPageCount() - 1) {
            pagination.setCurrentPageIndex(++index);
        }
        if (index == pagination.getPageCount() - 1) {
            startOrCancel.setText("开始");
        }

    }

    @FXML
    public void last(ActionEvent event) {
        var index = pagination.getCurrentPageIndex();
        if (index > 0) {
            pagination.setCurrentPageIndex(--index);
        }
        if (index < pagination.getPageCount() - 1) {
            startOrCancel.setText("取消");
        }
    }

    @FXML
    public void completeOrCancel(ActionEvent event) {
        var index = pagination.getCurrentPageIndex();
        if (index == pagination.getPageCount() - 1) {
            //start
        } else {
            stage.close();
        }
    }
}

package com.openjfx.database.app.skin;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.util.List;
import java.util.stream.Collectors;

/**
 * custom TableViewSkin
 *
 * @author yangkui
 * @since 1.0
 */
public class TableDataViewSkin extends TableViewSkin<StringProperty> {
    private final VirtualFlow<?> flowAlias;
    private final TableHeaderRow headerAlias;
    private Parent placeholderRegionAlias;
    private final ChangeListener<Boolean> visibleListener = (src, ov, nv) -> visibleChanged(nv);
    private final ListChangeListener<TableColumnHeader> tableColumnHeaderListChangeListener = (c) -> {
        while (c.next()) {
            if (c.wasAdded()) {
                var list = c.getAddedSubList();
                for (TableColumnHeader header : list) {
                    var tableColumn = header.getTableColumn();
                    tableColumn.setMaxWidth(200);
                    var text = tableColumn.getText();
                    var label = (Label) header.getChildrenUnmodifiable().get(0);
                    label.setPrefWidth(300);
                }
            }
        }
    };
    /**
     * <p>
     * Listener callback from children modifications.
     * Meant to find the placeholder when it is added.
     * This implementation passes all added sublists to
     * hasPlaceHolderRegion for search and install the
     * placeholder. Removes itself as listener if installed.
     * </p>
     */
    private final ListChangeListener<Node> childrenListener = c -> {
        while (c.next()) {
            if (c.wasAdded()) {
                if (installPlaceholderRegion(c.getAddedSubList())) {
                    uninstallChildrenListener();
                    return;
                }

            }
        }
    };

    /**
     * Instantiates the skin.
     *
     * @param table the table to skin.
     */
    public TableDataViewSkin(TableView table) {
        super(table);
        flowAlias = (VirtualFlow<?>) table.lookup(".virtual-flow");
        headerAlias = (TableHeaderRow) table.lookup(".column-header-background");

        /*
          start with a not-empty list, placeholder not yet instantiate to add a listener
          to the children until it will be added
         */
        if (!installPlaceholderRegion(getChildren())) {
            installChildrenListener();
        }

        tableColumnFitWith();
    }


    /**
     * Searches the given list for a Parent with style class "placeholder" and
     * wires its visibility handling if found.
     *
     * @param addedSubList
     * @return true if placeholder found and installed, false otherwise.
     */
    protected boolean installPlaceholderRegion(List<? extends Node> addedSubList) {
        if (placeholderRegionAlias != null) {
            throw new IllegalStateException("placeholder must not be installed more than once");
        }
        List<Node> parents = addedSubList.stream()
                .filter(e -> e.getStyleClass().contains("placeholder"))
                .collect(Collectors.toList());
        if (!parents.isEmpty()) {
            placeholderRegionAlias = (Parent) parents.get(0);
            placeholderRegionAlias.visibleProperty().addListener(visibleListener);
            visibleChanged(true);
            return true;
        }
        return false;
    }

    private void tableColumnFitWith() {
        var children = getChildren();
        var optional = children.stream().filter(e -> e.getStyleClass().contains("column-header-background")).findAny();
        if (optional.isPresent()) {
            var stackPane = (StackPane) optional.get();
            NestedTableColumnHeader nestedTableColumnHeader = null;
            for (Node child : stackPane.getChildren()) {
                if (child instanceof NestedTableColumnHeader) {
                    nestedTableColumnHeader = (NestedTableColumnHeader) child;
                    break;
                }
            }
            if (nestedTableColumnHeader == null) {
                throw new RuntimeException("not find nestedTableColumnHeader!!");
            }
            nestedTableColumnHeader.getColumnHeaders().addListener(tableColumnHeaderListChangeListener);
        }
    }


    protected void visibleChanged(Boolean nv) {
        if (nv) {
            flowAlias.setVisible(true);
            placeholderRegionAlias.setVisible(false);
        }
    }


    /**
     * Layout of flow unconditionally.
     */
    protected void layoutFlow(double x, double y, double width, double height) {
        // super didn't layout the flow if empty- do it now
        final double baselineOffset = getSkinnable().getLayoutBounds().getHeight() / 2;
        double headerHeight = headerAlias.getHeight();
        y += headerHeight;
        double flowHeight = Math.floor(height - headerHeight);
        layoutInArea(flowAlias, x, y, width, flowHeight, baselineOffset, HPos.CENTER, VPos.CENTER);
    }


    /**
     * Returns a boolean indicating whether the flow should be layout.
     * This implementation returns true if table is empty.
     */
    protected boolean shouldLayoutFlow() {
        return getItemCount() == 0;
    }

    @Override
    protected void layoutChildren(double x, double y, double width,
                                  double height) {
        super.layoutChildren(x, y, width, height);
        if (shouldLayoutFlow()) {
            layoutFlow(x, y, width, height);
        }
    }


    /**
     * Installs a ListChangeListener on the children which calls
     * childrenChanged on receiving change notification.
     */
    protected void installChildrenListener() {
        getChildren().addListener(childrenListener);
    }

    /**
     * Uninstalls a ListChangeListener on the children:
     */
    protected void uninstallChildrenListener() {
        getChildren().removeListener(childrenListener);
    }

}

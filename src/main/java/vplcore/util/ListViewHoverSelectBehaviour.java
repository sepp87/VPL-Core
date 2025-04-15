package vplcore.util;

import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author joostmeulenkamp
 */
public class ListViewHoverSelectBehaviour<T> {

    private final ListView listView;

    public ListViewHoverSelectBehaviour(ListView<T> listView) {
        this.listView = listView;
        this.listView.setOnMouseMoved(this::handle); // do NOT set event handlers on cell, because they are harder to remove and will cause memory leaks if not removed
    }

    private void handle(MouseEvent event) {
        double yPos = event.getY(); // Get the Y position of the mouse event relative to the ListView
        Integer firstVisibleIndex = ListViewUtils.getFirstVisibleCell(listView);
        if (firstVisibleIndex == null) {
            return;
        }
        double firstCellHeight = ListViewUtils.getVisibleHeightOfCell(listView, firstVisibleIndex);
        double cellHeight = ListViewUtils.getCellHeight(listView);
        int offset = (yPos > firstCellHeight) ? 1 : 0;
        int index = firstVisibleIndex + (int) ((yPos - firstCellHeight) / cellHeight) + offset; // Calculate the index of the item under the mouse
        if (index >= 0 && index < listView.getItems().size()) { // Ensure the index is within the bounds of the ListView's items
            listView.getSelectionModel().select(index); // Select the item at the calculated index
        }
    }

    public void remove() {
        listView.setOnMouseMoved(null);
    }

}

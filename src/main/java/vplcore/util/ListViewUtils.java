package vplcore.util;

import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;

/**
 *
 * @author Joost
 */
public class ListViewUtils {

    public static void scrollToWrapped(ListView listView, int direction, int rowsVisible) {
        int previousIndex = listView.getSelectionModel().getSelectedIndex();
        int newIndex = shiftIndexWrapped(previousIndex, direction, listView.getItems().size());

        listView.getSelectionModel().select(newIndex);

        int firstVisibleCell = getFirstVisibleCell(listView);
        int lastVisibleCell = firstVisibleCell + rowsVisible - 1;

        // Handle scrolling based on direction and index boundaries
        if (direction > 0) {
            // If wrapping around from last to first, scroll to the top
            if (previousIndex == listView.getItems().size() - 1 && newIndex == 0) {
                listView.scrollTo(0);
            } else if (newIndex > lastVisibleCell) {
                // Otherwise, gradually scroll down
                listView.scrollTo(firstVisibleCell + 1);
            }
        } else {
            // If wrapping around from first to last, scroll to the bottom
            if (previousIndex == 0 && newIndex == listView.getItems().size() - 1) {
                listView.scrollTo(listView.getItems().size() - 1);
            } else if (newIndex < firstVisibleCell) {
                // Otherwise, gradually scroll up
                listView.scrollTo(firstVisibleCell - 1);
            }
        }
    }

    public static int shiftIndex(int index, int amount, int size) {
        int newIndex = index + amount;

        // Ensure the new index stays within the bounds [0, size]
        newIndex = newIndex < 0 ? 0 : newIndex;
        newIndex = newIndex > size - 1 ? size - 1 : newIndex;
        return newIndex;
    }

    public static int shiftIndexWrapped(int index, int amount, int size) {
        int newIndex = (index + amount) % size; // Calculate the new index by adding the amount to the current index
        if (newIndex < 0) { // If the new index is negative, wrap it around to the end of the list
            newIndex += size;
        }
        return newIndex;
    }

    public static Integer getFirstVisibleCell(ListView listView) {
        ListViewSkin<?> skin = (ListViewSkin<?>) listView.getSkin(); // Access the ListView's skin to get the VirtualFlow
        if (skin == null) {
            return null; // If the skin is not set, return
        }
        VirtualFlow<?> virtualFlow = (VirtualFlow<?>) skin.getChildren().get(0);
        if (virtualFlow == null) {
            return null; // If the VirtualFlow is not found, return
        }
        return virtualFlow.getFirstVisibleCell().getIndex(); // Get the index of the first visible cell
    }

    // Method to determine the height of a cell in the ListView
    public static double getCellHeight(ListView<String> listView) {
        return listView.lookup(".list-cell").getLayoutBounds().getHeight();
    }
}

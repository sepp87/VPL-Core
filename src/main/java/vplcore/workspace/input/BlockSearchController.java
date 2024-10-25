package vplcore.workspace.input;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ChangeListener;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.editor.EditorView;
import vplcore.graph.model.Block;
import vplcore.graph.util.BlockFactory;
import vplcore.graph.util.BlockLoader;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class BlockSearchController {

    private static final double OFFSET = 20;
    private static final int ROWS_VISIBLE = 17;

    private final Workspace workspace;
    private final BlockSearchView view;

    private Point2D creationPoint;

    private final TextField searchField;
    private final ListView<String> listView;
    private final ReadOnlyIntegerProperty currentIndex;

    private final ChangeListener<Boolean> searchFieldFocusChangedListener;

    public BlockSearchController(Workspace workspace, BlockSearchView blockSearchView) {
        this.workspace = workspace;
        this.view = blockSearchView;

        searchField = view.getSearchField();
        listView = view.getListView();

        searchField.setOnKeyPressed(this::handleSearchFieldKeyPressed);
        searchField.textProperty().addListener(this::handleSearchTermChanged);
        searchFieldFocusChangedListener = this::handleSearchFieldFocusChanged;

        listView.setItems(BlockLoader.BLOCK_TYPE_LIST);
        listView.setOnMouseClicked(this::handleListViewClicked);
        listView.setOnMouseMoved(this::handleListViewHovered);

        currentIndex = listView.getSelectionModel().selectedIndexProperty();
    }

    private void handleSearchTermChanged(Object b, String o, String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        if (searchTerm.isBlank()) {
            listView.setItems(BlockLoader.BLOCK_TYPE_LIST);
            listView.getSelectionModel().select(-1);
            return;
        }

        ObservableList<String> result = observableArrayList();
        for (String type : BlockLoader.BLOCK_TYPE_LIST) {
            if (type.toLowerCase().contains(searchTerm)) {
                result.add(type);
            }
        }

        listView.setItems(result);
        listView.getSelectionModel().selectFirst();
    }

    private void handleSearchFieldKeyPressed(KeyEvent event) {
        switch (event.getCode()) {
            case DOWN, UP -> {
                int direction = event.getCode() == KeyCode.DOWN ? 1 : -1;
                int index = shiftIndex(direction);
                listView.getSelectionModel().select(index);

                int firstVisibleCell = getFirstVisibleCell();
                int lastVisibleCell = firstVisibleCell + ROWS_VISIBLE - 1;

                // If scrolling down, scroll only if the selected index is beyond the last visible cell
                if (direction > 0 && index > lastVisibleCell) {
                    listView.scrollTo(firstVisibleCell + direction);
                } // If scrolling up, scroll only if the selected index is before the first visible cell
                else if (direction < 0 && index < firstVisibleCell) {
                    listView.scrollTo(firstVisibleCell + direction);
                }
            }
            case ESCAPE ->
                hideView();
            case ENTER ->
                createBlock();
        }
        event.consume(); // Consume the event so space does not trigger zoom to fit
    }

    public int shiftIndex(int amount) {
        int size = listView.getItems().size();
        int index = currentIndex.get() + amount;

        // Ensure the new index stays within the bounds [0, size]
        index = index < 0 ? 0 : index;
        index = index > size - 1 ? size - 1 : index;
        return index;
    }

    private void handleListViewClicked(MouseEvent event) {
        createBlock();
    }

    private void createBlock() {
        String blockIdentifier = listView.getSelectionModel().getSelectedItem();
        if (blockIdentifier == null) {
            return;
        }
        System.out.println("Create block " + blockIdentifier);

        Block block = BlockFactory.createBlock(blockIdentifier, workspace);
        block.setLayoutX(creationPoint.getX() - OFFSET);
        block.setLayoutY(creationPoint.getY() - OFFSET);

        workspace.getChildren().add(block);
        workspace.blockSet.add(block);

        hideView();
    }

    private void handleListViewHovered(MouseEvent event) {
        double yPos = event.getY(); // Get the Y position of the mouse event relative to the ListView
        Integer firstVisibleIndex = getFirstVisibleCell();
        if (firstVisibleIndex == null) {
            return;
        }
        int index = firstVisibleIndex + (int) (yPos / getCellHeight(listView)); // Calculate the index of the item under the mouse
        if (index >= 0 && index < listView.getItems().size()) { // Ensure the index is within the bounds of the ListView's items
            listView.getSelectionModel().select(index); // Select the item at the calculated index
        }
    }

    private Integer getFirstVisibleCell() {
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
    private double getCellHeight(ListView<String> listView) {
        return listView.lookup(".list-cell").getLayoutBounds().getHeight();
    }

    public void handleMouseClicked(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof Workspace;
        boolean onBlockSearch = Workspace.checkParents(intersectedNode, BlockSearchView.class);
        boolean isDoublePrimaryClick = event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && event.isStillSincePress();
        boolean mouseIsIdle = workspace.getMouseMode() == MouseMode.MOUSE_IDLE || workspace.getMouseMode() == MouseMode.AWAITING_SELECT_BLOCK;

        if (isDoublePrimaryClick && onEditorOrWorkspace && mouseIsIdle) {
            showView(event.getSceneX(), event.getSceneY());
        } else if (onBlockSearch) {
            // keep block search shown if it is clicked on
        } else {
            hideView();
        }
    }

    private void showView(double x, double y) {
        workspace.setMouseMode(MouseMode.AWAITING_SELECT_BLOCK);
        creationPoint = workspace.sceneToLocal(x - OFFSET, y - OFFSET);
        view.setVisible(true);
        view.setTranslateX(x - OFFSET);
        view.setTranslateY(y - OFFSET);
        searchField.requestFocus();
        searchField.focusedProperty().addListener(searchFieldFocusChangedListener);
        listView.setPrefHeight(getCellHeight(listView) * ROWS_VISIBLE);
        listView.getSelectionModel().select(-1);
        listView.scrollTo(-1);
    }

    private void hideView() {
        view.setVisible(false);
        workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        searchField.setText("");
        searchField.focusedProperty().removeListener(searchFieldFocusChangedListener);
    }

    private void handleSearchFieldFocusChanged(Object b, boolean o, boolean isFocused) {
        if (!isFocused) {
            searchField.requestFocus();
        }
    }
}

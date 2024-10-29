package vplcore.editor;

import javafx.beans.value.ChangeListener;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.graph.util.BlockLoader;
import vplcore.util.ListViewUtils;
import vplcore.util.NodeHierarchyUtils;
import vplcore.workspace.ActionManager;
import vplcore.workspace.Workspace;
import vplcore.workspace.MouseMode;
import vplcore.workspace.command.CreateBlockCommand;

/**
 *
 * @author Joost
 */
public class BlockSearchController {

    private static final double OFFSET = 20;
    private static final int ROWS_VISIBLE = 17;

    private final EditorModel editorModel;
    private final ActionManager actionManager;
    private final BlockSearchView view;

    private Point2D creationPoint;

    private final TextField searchField;
    private final ListView<String> listView;

    private final ChangeListener<Boolean> searchFieldFocusChangedListener;

    public BlockSearchController(EditorModel editorModel, BlockSearchView blockSearchView, ActionManager actionManager) {
        this.editorModel = editorModel;
        this.actionManager = actionManager;
        this.view = blockSearchView;

        searchField = view.getSearchField();
        listView = view.getListView();

        searchField.setOnKeyPressed(this::handleShortcutAction);
        searchField.textProperty().addListener(this::handleSearchAction);
        searchFieldFocusChangedListener = this::handleRetainFocus;

        listView.setItems(BlockLoader.BLOCK_TYPE_LIST);
        listView.setOnMouseClicked(this::handleCreateBlock);
        listView.setOnMouseMoved(this::handleSelectHoveredItem);

    }

    private void handleSearchAction(Object b, String o, String searchTerm) {
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

    private void handleShortcutAction(KeyEvent event) {
        switch (event.getCode()) {
            case DOWN, UP -> {
                int direction = event.getCode() == KeyCode.DOWN ? 1 : -1;
                ListViewUtils.scrollToWrapped(listView, direction, ROWS_VISIBLE);
            }
            case ESCAPE ->
                hideView();
            case ENTER ->
                createBlock();
        }
        event.consume(); // Consume the event so space does not trigger zoom to fit
    }

    private void handleCreateBlock(MouseEvent event) {
        createBlock();
    }

    private void createBlock() {
        String blockIdentifier = listView.getSelectionModel().getSelectedItem();
        if (blockIdentifier == null) {
            return;
        }

        System.out.println("Create block " + blockIdentifier);
        CreateBlockCommand createBlockCommand = new CreateBlockCommand(blockIdentifier, creationPoint, actionManager.getWorkspace());
        actionManager.executeCommand(createBlockCommand);

        hideView();
    }

    private void handleSelectHoveredItem(MouseEvent event) {
        double yPos = event.getY(); // Get the Y position of the mouse event relative to the ListView
        Integer firstVisibleIndex = ListViewUtils.getFirstVisibleCell(listView);
        if (firstVisibleIndex == null) {
            return;
        }
        int index = firstVisibleIndex + (int) (yPos / ListViewUtils.getCellHeight(listView)); // Calculate the index of the item under the mouse
        if (index >= 0 && index < listView.getItems().size()) { // Ensure the index is within the bounds of the ListView's items
            listView.getSelectionModel().select(index); // Select the item at the calculated index
        }
    }

    public void processEditorMouseClicked(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof Workspace;
        boolean onBlockSearch = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, BlockSearchView.class);
        boolean isDoublePrimaryClick = event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && event.isStillSincePress();
        boolean isIdle = editorModel.modeProperty().get() == EditorMode.IDLE_MODE;

        if (isDoublePrimaryClick && onEditorOrWorkspace && isIdle) {
            showView(event.getSceneX(), event.getSceneY());
        } else if (onBlockSearch) {
            // keep block search shown if it is clicked on
        } else {
            hideView();
        }
    }

    private void showView(double x, double y) {
        editorModel.modeProperty().set(EditorMode.BLOCK_SEARCH_MODE);
        creationPoint = new Point2D(x - OFFSET, y - OFFSET);
        view.setVisible(true);
        view.setTranslateX(x - OFFSET);
        view.setTranslateY(y - OFFSET);
        searchField.requestFocus();
        searchField.focusedProperty().addListener(searchFieldFocusChangedListener);
        listView.setPrefHeight(ListViewUtils.getCellHeight(listView) * ROWS_VISIBLE);
        listView.getSelectionModel().select(-1);
        listView.scrollTo(-1);
    }

    private void hideView() {
        view.setVisible(false);
        editorModel.modeProperty().set(EditorMode.IDLE_MODE);
        searchField.setText("");
        searchField.focusedProperty().removeListener(searchFieldFocusChangedListener);
    }

    private void handleRetainFocus(Object b, boolean o, boolean isFocused) {
        if (!isFocused) {
            searchField.requestFocus();
        }
    }
}

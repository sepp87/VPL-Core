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
import static vplcore.Util.scrollToWrapped;
import vplcore.graph.model.Block;
import vplcore.graph.util.BlockFactory;
import vplcore.graph.util.BlockLoader;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;

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

    private final ChangeListener<Boolean> searchFieldFocusChangedListener;

    public BlockSearchController(Workspace workspace, BlockSearchView blockSearchView) {
        this.workspace = workspace;
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
                scrollToWrapped(listView, direction, ROWS_VISIBLE);
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

        Block block = BlockFactory.createBlock(blockIdentifier, workspace);
        block.setLayoutX(creationPoint.getX() - OFFSET);
        block.setLayoutY(creationPoint.getY() - OFFSET);

        workspace.getChildren().add(block);
        workspace.blockSet.add(block);

        hideView();
    }

    private void handleSelectHoveredItem(MouseEvent event) {
        double yPos = event.getY(); // Get the Y position of the mouse event relative to the ListView
        Integer firstVisibleIndex = vplcore.Util.getFirstVisibleCell(listView);
        if (firstVisibleIndex == null) {
            return;
        }
        int index = firstVisibleIndex + (int) (yPos / vplcore.Util.getCellHeight(listView)); // Calculate the index of the item under the mouse
        if (index >= 0 && index < listView.getItems().size()) { // Ensure the index is within the bounds of the ListView's items
            listView.getSelectionModel().select(index); // Select the item at the calculated index
        }
    }

    public void processEditorMouseClicked(MouseEvent event) {
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
        listView.setPrefHeight(vplcore.Util.getCellHeight(listView) * ROWS_VISIBLE);
        listView.getSelectionModel().select(-1);
        listView.scrollTo(-1);
    }

    private void hideView() {
        view.setVisible(false);
        workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        searchField.setText("");
        searchField.focusedProperty().removeListener(searchFieldFocusChangedListener);
    }

    private void handleRetainFocus(Object b, boolean o, boolean isFocused) {
        if (!isFocused) {
            searchField.requestFocus();
        }
    }
}

package vplcore.editor;

import javafx.beans.value.ChangeListener;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import vplcore.App;
import vplcore.graph.util.BlockLibraryLoader;
import vplcore.util.ListViewUtils;
import vplcore.util.NodeHierarchyUtils;
import vplcore.context.ActionManager;
import vplcore.context.EventRouter;
import vplcore.context.StateManager;
import vplcore.context.command.CreateBlockCommand;
import static vplcore.util.EditorUtils.onFreeSpace;
import static vplcore.util.EventUtils.isDoubleClick;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class BlockSearchController extends BaseController {

    private static final double OFFSET = 20;
    private static final int ROWS_VISIBLE = 17;

    private final EventRouter eventRouter;
    private final ActionManager actionManager;
    private final StateManager state;
    private final BlockSearchView view;

    private Point2D creationPoint;

    private final TextField searchField;
    private final ListView<String> listView;

    private final ChangeListener<Boolean> searchFieldFocusChangedListener;

    public BlockSearchController(String contextId, BlockSearchView blockSearchView) {
        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.actionManager = App.getContext(contextId).getActionManager();
        this.state = App.getContext(contextId).getStateManager();

        this.view = blockSearchView;

        searchField = view.getSearchField();
        listView = view.getListView();

        searchField.setOnKeyPressed(this::handleShortcutAction);
        searchField.textProperty().addListener(this::handleSearchAction);
        searchFieldFocusChangedListener = this::handleRetainFocus;

        listView.setItems(BlockLibraryLoader.BLOCK_TYPE_LIST);
        listView.setOnMouseClicked(this::handleCreateBlock);
//        listView.setOnMouseMoved(this::handleSelectHoveredItem);
        listView.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item);
                }
            };

            // Do NOT use onMouseEntered, otherwise the selection keeps jumping back to the cell underneath the mouse whilst navigating with keys up and down
            cell.setOnMouseMoved(e -> {
                if (!cell.isEmpty()) {
                    lv.getSelectionModel().select(cell.getIndex());
                }
            });

            return cell;
        });

        eventRouter.addEventListener(MouseEvent.MOUSE_CLICKED, this::toggleBlockSearch);
    }

    private void toggleBlockSearch(MouseEvent event) {
        if (isDoubleClick(event) && onFreeSpace(event) && state.isIdle()) {
            showView(event.getSceneX(), event.getSceneY());

        } else if (view.isVisible() && !NodeHierarchyUtils.isPickedNodeOrParentOfType(event, BlockSearchView.class)) {
            // hide block search if it is shown and the click was somewhere else 
            hideView();
        }
    }

    private void showView(double x, double y) {
        state.setAwaitingBlockSearch();
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
        state.setIdle();
        searchField.setText("");
        searchField.focusedProperty().removeListener(searchFieldFocusChangedListener);
    }

    private void handleRetainFocus(Object b, boolean o, boolean isFocused) {
        if (!isFocused) {
            searchField.requestFocus();
        }
    }

    private void handleSearchAction(Object b, String o, String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        if (searchTerm.isBlank()) {
            listView.setItems(BlockLibraryLoader.BLOCK_TYPE_LIST);
            listView.getSelectionModel().select(-1);
            return;
        }

        ObservableList<String> result = observableArrayList();
        for (String type : BlockLibraryLoader.BLOCK_TYPE_LIST) {
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
        WorkspaceController workspaceController = actionManager.getWorkspaceController();
        Point2D location = workspaceController.getView().sceneToLocal(creationPoint);
        CreateBlockCommand createBlockCommand = new CreateBlockCommand(actionManager.getWorkspaceModel(), blockIdentifier, location);
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

}

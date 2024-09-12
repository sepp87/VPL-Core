package vplcore.graph.util;

import vplcore.graph.model.Block;
import static java.util.stream.Collectors.toCollection;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.ListCell;

import javafx.scene.input.*;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public class SelectBlock extends Block {

    private ListView<String> listView;
    private final TextField searchField;
    
   private final EventHandler<MouseEvent> selectBlockExitedHandler = this::handleSelectBlockExited;
   private final EventHandler<MouseEvent> selectBlockEnteredHandler = this::handleSelectBlockEntered;
   private final EventHandler<MouseEvent> listViewPressedHandler = this::handleListViewPressed;
   private final EventHandler<MouseEvent> listViewHoveredHandler = this::handleListViewHovered;
   private final EventHandler<KeyEvent> searchFieldKeyPressedHandler = this::handleSearchFieldKeyPressed;
   private final EventHandler<KeyEvent> searchFieldKeyReleasedHandler = this::handleSearchFieldKeyReleased;

    /**
     * Select block is used to pick a block type and place it on the host
     * canvas. It reads out classes inside the package and adds them to a list
     * view.
     *
     * @param workspace
     */
    public SelectBlock(Workspace workspace) {
        super(workspace);

        searchField = new TextField();
        searchField.setMaxWidth(140);
        searchField.setPromptText("Search...");

        listView = new ListView<>();
        listView.layoutBoundsProperty().addListener(e -> {
            ScrollBar scrollBarv = (ScrollBar) listView.lookup(".scroll-bar:vertical");
            scrollBarv.setDisable(true);
        });

        listView.setMaxWidth(240);
        listView.setPrefHeight(265);

        listView.setItems(BlockLoader.BLOCK_TYPE_LIST);

        this.setOnMouseExited(selectBlockExitedHandler);
        this.setOnMouseDragExited(selectBlockExitedHandler);
        this.setOnMouseEntered(selectBlockEnteredHandler);
        listView.setOnMousePressed(listViewPressedHandler);
        listView.setOnMouseMoved(listViewHoveredHandler);
        searchField.setOnKeyPressed(searchFieldKeyPressedHandler);
        searchField.setOnKeyReleased(searchFieldKeyReleasedHandler);

        VBox searchBox = new VBox(10);
        searchBox.getChildren().addAll(searchField, listView);

        mainContentGrid.setStyle("-fx-padding: 0;");

        addControlToBlock(searchBox);
    }

    /**
     * Place focus back on search field when selecting a list item
     *
     * @param e
     */
    private void handleListViewPressed(MouseEvent e) {
        searchField.requestFocus();

        if (e.getButton() != MouseButton.PRIMARY) {
            e.consume();
            return;
        }

        if (e.getClickCount() == 2) {
            createBlock();
        }
    }

    private void handleListViewHovered(MouseEvent event) {

        // Get the Y position of the mouse event relative to the ListView
        double yPos = event.getY();

        // Calculate the index of the item under the mouse
        int index = (int) (yPos / getCellHeight(listView));

        // Ensure the index is within the bounds of the ListView's items
        if (index >= 0 && index < listView.getItems().size()) {
            // Get the item at the calculated index
            String hoveredItem = listView.getItems().get(index);
            listView.getSelectionModel().select(index);
        }
    }

    // Method to determine the height of a cell in the ListView
    private double getCellHeight(ListView<String> listView) {
        return listView.getFixedCellSize() > 0 ? listView.getFixedCellSize() : 24; // Default to 24 if height is 0
    }

    /**
     * Search for a particular block type matching the search phrase
     *
     * @param keyEvent
     */
    private void handleSearchFieldKeyReleased(KeyEvent keyEvent) {
        String keyWord = searchField.getText();

        if (!"".equals(keyWord)) {
            /**
             * Regular expression to filter the list with. (?i) : makes it
             * ignore case \Q ... \E : takes care of possible special characters
             * in keyWord . : means any character * : zero to multiple times
             * recurring
             */

            String regex = "(?si).*\\Q" + keyWord + "\\E.*";

            /**
             * search the list by keywords and set the search results as
             * temporary list in the listView
             */
            listView.setItems(BlockLoader.BLOCK_TYPE_LIST.stream()
                    .filter(x -> x.matches(regex))
                    .collect(toCollection(FXCollections::observableArrayList)));

            if (listView.getSelectionModel().getSelectedIndex() == -1) {
                listView.getSelectionModel().selectFirst();
            }

        } else {
            listView.setItems(BlockLoader.BLOCK_TYPE_LIST);
        }
    }

    /**
     * Handle key events in text field. Up and down changes the selected list
     * index. Enter confirms choice of selection and generates a type of block.
     *
     * @param keyEvent
     */
    private void handleSearchFieldKeyPressed(KeyEvent keyEvent) {

        KeyCode key = keyEvent.getCode();

        int listSize = listView.getItems().size();
        int listIndex = listView.getSelectionModel().getSelectedIndex();

        if (key == KeyCode.ENTER && listSize > 0) {
            if ((!searchField.getText().equals(""))
                    || (listIndex > -1 && listIndex < listSize)) {
                createBlock();

            }
        } else {
            switch (key) {
                case DOWN:
                    if (listIndex < listSize) {
                        listView.getSelectionModel().select(listIndex + 1);
                        keyEvent.consume();
                    }
                    break;

                case UP:
                    if (listIndex > 0) {
                        listView.getSelectionModel().select(listIndex - 1);
                    }
                    keyEvent.consume();
                    break;
            }
        }
    }

    /**
     * Create a block of the selected type and insert it at the mouse position
     */
    private void createBlock() {
        String blockIdentifier = listView.getSelectionModel().getSelectedItem();

        if (blockIdentifier == null) {
            return;
        }

        Block block = BlockFactory.createBlock(blockIdentifier, workspace);

        if (block == null) {
            System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
            return;
        }

        Point2D position = workspace.mouse.getPosition();
        block.setLayoutX(position.getX() - 20);
        block.setLayoutY(position.getY() - 20);

        workspace.getChildren().add(block);
        workspace.blockSet.add(block);
        hide();
    }

    /**
     * Remove block from host canvas if user move his mouse outside of the
     * panel. Event also gets fired when deleted by other means, which will lead
     * to a Duplicate Children Added Exception when from createBlock() and a
     * Array Out Of Bounds Exception in searchField_KeyPress(). Check against
     * the removed boolean prevents this from happening.
     *
     * @param e
     */
    private void handleSelectBlockExited(MouseEvent e) {
        hide();
        e.consume();
    }

    private void hide() {
        this.setVisible(false);
        searchField.setText(null);
        listView.setItems(BlockLoader.BLOCK_TYPE_LIST);
        listView.getSelectionModel().select(null);
    }

    /**
     * Search field requests focus on opening of select block so user can start
     * typing
     *
     * @param e
     */
    private void handleSelectBlockEntered(MouseEvent e) {
        TextField text = (TextField) controls.get(0).getChildrenUnmodifiable().get(0);
        text.requestFocus();
    }

    @Override
    public void calculate() {
    }

    @Override
    public Block clone() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

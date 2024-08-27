package vplcore.graph.util;

import vplcore.graph.model.Block;
import static java.util.stream.Collectors.toCollection;
import javafx.collections.FXCollections;

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
    private TextField searchField;

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

        this.setOnMouseExited(this::selectBlock_MouseExit);
        this.setOnMouseDragExited(this::selectBlock_MouseExit);
        this.setOnMouseEntered(this::selectBlock_MouseEnter);
        listView.setOnMousePressed(this::listView_MousePress);
        searchField.setOnKeyPressed(this::searchField_KeyPress);
        searchField.setOnKeyReleased(this::searchField_KeyRelease);

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
    private void listView_MousePress(MouseEvent e) {
        searchField.requestFocus();

        if (e.getButton() != MouseButton.PRIMARY) {
            e.consume();
            return;
        }

        if (e.getClickCount() == 2) {
            createBlock();
        }
    }

    /**
     * Search for a particular block type matching the search phrase
     *
     * @param e
     */
    private void searchField_KeyRelease(KeyEvent e) {
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
     * @param event
     */
    private void searchField_KeyPress(KeyEvent event) {

        KeyCode key = event.getCode();

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
                        event.consume();
                    }
                    break;

                case UP:
                    if (listIndex > 0) {
                        listView.getSelectionModel().select(listIndex - 1);
                    }
                    event.consume();
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
        
        block.setLayoutX(workspace.mouse.mousePosition.getX() - 20);
        block.setLayoutY(workspace.mouse.mousePosition.getY() - 20);

        workspace.getChildren().add(block);
        workspace.blockSet.add(block);
        removed = true;
        workspace.getChildren().remove(this);
    }

    boolean removed = false;

    /**
     * Remove block from host canvas if user move his mouse outside of the
     * panel. Event also gets fired when deleted by other means, which will lead
     * to a Duplicate Children Added Exception when from createBlock() and a
     * Array Out Of Bounds Exception in searchField_KeyPress(). Check against
     * the removed boolean prevents this from happening.
     *
     * @param e
     */
    private void selectBlock_MouseExit(MouseEvent e) {
        if (!removed) {
            workspace.getChildren().remove(this);
        }
        e.consume();

    }

    /**
     * Search field requests focus on opening of select block so user can start
     * typing
     *
     * @param e
     */
    private void selectBlock_MouseEnter(MouseEvent e) {
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

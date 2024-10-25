package vplcore.workspace.input;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import vplcore.graph.util.BlockLoader;

/**
 *
 * @author Joost
 */
public class BlockSearchView extends VBox {

    private final TextField searchField;
    private final ListView listView;
    
    public BlockSearchView() {
        
        searchField = new TextField();
//        searchField.setMaxWidth(140);
        searchField.setPromptText("Search...");
        
        listView = new ListView<>();
//        listView.setMaxWidth(240);
//        listView.setPrefHeight(265);
        listView.setItems(BlockLoader.BLOCK_TYPE_LIST);

        this.setVisible(false);
        this.getStyleClass().add("block-search-box");
        this.setSpacing(10);
        this.getChildren().addAll(searchField, listView);
    }
    
    public TextField getSearchField() {
        return searchField;
    }
    
    public ListView getListView() {
        return listView;
    }
}

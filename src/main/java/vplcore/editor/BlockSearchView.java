package vplcore.editor;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import vplcore.graph.util.BlockLibraryLoader;

/**
 *
 * @author Joost
 */
public class BlockSearchView extends VBox {

    private final TextField searchField;
    private final ListView<String> listView;
    
    public BlockSearchView() {
        
        searchField = new TextField();
        searchField.setPromptText("Search...");
        
        listView = new ListView<>();
//        listView.setItems(BlockLibraryLoader.BLOCK_TYPE_LIST);

        this.setVisible(false);
        this.getStyleClass().add("block-search");
        this.setSpacing(10);
        this.getChildren().addAll(searchField, listView);
    }
    
    public TextField getSearchField() {
        return searchField;
    }
    
    public ListView<String> getListView() {
        return listView;
    }
}

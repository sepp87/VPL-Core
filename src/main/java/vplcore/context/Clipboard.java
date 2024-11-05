package vplcore.context;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import vplcore.graph.model.Block;

/**
 *
 * @author Joost
 */
public class Clipboard {

    private static final ObservableSet<Block> blocksOnClipboard = FXCollections.observableSet();
    private static final ObservableSet<Block> connectionsOnClipboard = FXCollections.observableSet();
    
    public static final boolean containsItems() {
        return !blocksOnClipboard.isEmpty();
    }
    
    public static final void clear() {
        blocksOnClipboard.clear();
        connectionsOnClipboard.clear();
    }
    
    public static final void addBlocks(Collection<Block> blocks) {
        clear();
        blocksOnClipboard.addAll(blocks);
        // TODO unimplemented: filter only unique connections
    }
    
}

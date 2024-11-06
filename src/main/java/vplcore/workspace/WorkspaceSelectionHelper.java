package vplcore.workspace;

import java.util.ArrayList;
import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import vplcore.graph.model.Block;

/**
 *
 * @author Joost
 */
public class WorkspaceSelectionHelper {

    private final WorkspaceModel model;
    private final WorkspaceView view;

    private final ObservableSet<Block> blocksSelectedOnWorkspace = FXCollections.observableSet();

    public WorkspaceSelectionHelper(WorkspaceModel workspaceModel, WorkspaceView workspaceView) {
        this.model = workspaceModel;
        this.view = workspaceView;
    }

    public void selectAllBlocks() {
        this.blocksSelectedOnWorkspace.clear();
        for (Block block : model.getBlocks()) {
            block.setSelected(true);
            this.blocksSelectedOnWorkspace.add(block);
        }
    }

    public void deselectAllBlocks() {
        for (Block block : this.blocksSelectedOnWorkspace) {
            block.setSelected(false);
        }
        this.blocksSelectedOnWorkspace.clear();
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        for (Block block : model.getBlocks()) {
            if (true // unnecessary statement for readability
                    && block.getLayoutX() >= selectionMin.getX()
                    && block.getLayoutX() + block.getWidth() <= selectionMax.getX()
                    && block.getLayoutY() >= selectionMin.getY()
                    && block.getLayoutY() + block.getHeight() <= selectionMax.getY()) {

                this.blocksSelectedOnWorkspace.add(block);
                block.setSelected(true);

            } else {
                this.blocksSelectedOnWorkspace.remove(block);
                block.setSelected(false);
            }
        }
    }

    public void selectBlock(Block block) {
        block.setSelected(true);
        blocksSelectedOnWorkspace.add(block);
    }

    public void deselectBlock(Block block) {
        block.setSelected(false);
        blocksSelectedOnWorkspace.remove(block);
    }

    public Collection<Block> getBlocks() {
        return model.getBlocks();
    }

    public Collection<Block> getSelectedBlocks() {
        return new ArrayList<>(blocksSelectedOnWorkspace);
    }
}

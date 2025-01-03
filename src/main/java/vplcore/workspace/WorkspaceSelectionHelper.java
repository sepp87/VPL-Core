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
    private final WorkspaceController controller;

    private final ObservableSet<Block> blocksSelectedOnWorkspace = FXCollections.observableSet();
    private final ObservableSet<BlockController> selectedBlocks = FXCollections.observableSet();

    public WorkspaceSelectionHelper(WorkspaceModel workspaceModel, WorkspaceView workspaceView, WorkspaceController workspaceController) {
        this.model = workspaceModel;
        this.view = workspaceView;
        this.controller = workspaceController;
    }

    public void selectAllBlocks() {
        if (vplcore.App.BLOCK_MVC) {
            this.selectedBlocks.clear();
            for (BlockController block : controller.getBlockControllers()) {
                block.selectedProperty().set(true);
                selectedBlocks.add(block);
            }
        } else {
            this.blocksSelectedOnWorkspace.clear();
            for (Block block : model.getBlocks()) {
                block.setSelected(true);
                this.blocksSelectedOnWorkspace.add(block);
            }
        }
    }

    public void deselectAllBlocks() {
        if (vplcore.App.BLOCK_MVC) {
            for (BlockController block : selectedBlocks) {
                block.selectedProperty().set(false);
            }
            this.selectedBlocks.clear();
        } else {
            for (Block block : this.blocksSelectedOnWorkspace) {
                block.setSelected(false);
            }
            this.blocksSelectedOnWorkspace.clear();
        }

    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {

        if (vplcore.App.BLOCK_MVC) {
            for (BlockModel block : model.getBlockModels()) {
                BlockController blockController = controller.getBlockController(block);
                BlockView blockView = blockController.getView();
                if (true // unnecessary statement for readability
                        && block.layoutXProperty().get() >= selectionMin.getX()
                        && block.layoutXProperty().get() + blockView.getWidth() <= selectionMax.getX()
                        && block.layoutYProperty().get() >= selectionMin.getY()
                        && block.layoutYProperty().get() + blockView.getHeight() <= selectionMax.getY()) {

                    selectedBlocks.add(blockController);
                    blockController.selectedProperty().set(true);

                } else {
                    selectedBlocks.remove(blockController);
                    blockController.selectedProperty().set(false);
                }
            }
        } else {
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
    }

    public void selectBlock(Block block) {
        block.setSelected(true);
        blocksSelectedOnWorkspace.add(block);
    }

    public void deselectBlock(Block block) {
        block.setSelected(false);
        blocksSelectedOnWorkspace.remove(block);
    }

    public void selectBlock(BlockController block) {
        block.selectedProperty().set(true);
        selectedBlocks.add(block);
    }

    public void deselectBlock(BlockController block) {
        block.selectedProperty().set(false);
        selectedBlocks.remove(block);
    }

    public Collection<Block> getSelectedBlocks() {
        return new ArrayList<>(blocksSelectedOnWorkspace);
    }

    public Collection<BlockController> getSelectedBlockControllers() {
        return new ArrayList<>(selectedBlocks);
    }
}

package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import vplcore.graph.model.Block;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.BlockModel;
import vplcore.workspace.BlockView;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class AlignLeftCommand implements Undoable {

    private final WorkspaceController workspace;

    public AlignLeftCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        if (vplcore.App.BLOCK_MVC) {
            List<BlockView> blockViews = new ArrayList<>();
            for (BlockController blockController : workspace.getSelectedBlockControllers()) {
                blockViews.add(blockController.getView());
            }
            Bounds bBox = BlockView.getBoundingBoxOfBlocks(blockViews);
            for (BlockController blockController : workspace.getSelectedBlockControllers()) {
                BlockModel blockModel = blockController.getModel();
                blockModel.layoutXProperty().set(bBox.getMinX());
            }
        } else {
            Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.getSelectedBlocks());
            for (Block block : workspace.getSelectedBlocks()) {
                block.setLayoutX(bBox.getMinX());
            }
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

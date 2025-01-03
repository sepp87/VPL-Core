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
public class AlignBottomCommand implements Undoable {

    private final WorkspaceController workspace;

    public AlignBottomCommand(WorkspaceController workspace) {
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
                BlockView blockView = blockController.getView();
                blockModel.layoutYProperty().set(bBox.getMaxY() - blockView.getHeight());
            }
        } else {
            Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.getSelectedBlocks());
            for (Block block : workspace.getSelectedBlocks()) {
                block.setLayoutY(bBox.getMaxY() - block.getHeight());
            }
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

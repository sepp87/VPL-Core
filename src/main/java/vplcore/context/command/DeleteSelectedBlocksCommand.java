package vplcore.context.command;

import java.util.Collection;
import vplcore.graph.model.Block;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class DeleteSelectedBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;

    public DeleteSelectedBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public void execute() {

        if (vplcore.App.BLOCK_MVC) {
            Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
            workspaceController.deselectAllBlocks();
            for (BlockController blockController : selectedBlockControllers) {
                blockController.getModel().remove();
            }
        } else {
            Collection<Block> selectedBlocks = workspaceController.getSelectedBlocks();
            workspaceController.deselectAllBlocks();
            for (Block block : selectedBlocks) {
                block.delete();
            }
        }

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

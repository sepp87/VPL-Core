package vplcore.context.command;

import java.util.Collection;
import vplcore.App;
import vplcore.graph.model.Block;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

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
            if (App.FUTURE_TESTS) {
                System.out.println("DeleteSelectedBlocksCommand");
            }
            Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
            workspaceController.deselectAllBlocks();
            WorkspaceModel workspaceModel = workspaceController.getModel();
            for (BlockController blockController : selectedBlockControllers) {
                workspaceModel.removeBlockModel(blockController.getModel());
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

package vplcore.context.command;

import java.util.Collection;
import vplcore.App;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class RemoveSelectedBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;

    public RemoveSelectedBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public void execute() {
        if (App.FUTURE_TESTS) {
            System.out.println("DeleteSelectedBlocksCommand");
        }
        Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
        workspaceController.deselectAllBlocks();
        WorkspaceModel workspaceModel = workspaceController.getModel();
        for (BlockController blockController : selectedBlockControllers) {
            BlockModel blockModel = blockController.getModel();
            workspaceModel.removeBlockModel(blockModel);
            workspaceModel.removeBlockFromGroup(blockModel);
            workspaceModel.removeConnectionModels(blockModel);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

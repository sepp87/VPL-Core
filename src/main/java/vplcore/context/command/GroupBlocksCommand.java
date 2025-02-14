package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.BlockGroupModel;
import vplcore.workspace.BlockModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class GroupBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;

    public GroupBlocksCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
    }

    @Override
    public void execute() {
        if (vplcore.App.BLOCK_MVC) {
            System.out.println("Group Blocks");
            Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
            // do not execute if selected blocks is less than two
            if (selectedBlockControllers.size() < 2) {
                return;
            }
            List<BlockModel> selectedBlockModels = new ArrayList<>();
            for (BlockController blockController : selectedBlockControllers) {
                selectedBlockModels.add(blockController.getModel());
            }
            BlockGroupModel blockGroupModel = new BlockGroupModel(workspaceController.getContextId(), workspaceController, workspaceModel);
            blockGroupModel.setChildBlocks(selectedBlockModels);
            workspaceModel.addBlockGroupModel(blockGroupModel);
        } else {
            workspaceController.addBlockGroup();
        }

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

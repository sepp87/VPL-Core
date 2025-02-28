package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;
import vplcore.graph.group.BlockGroupModel;
import vplcore.graph.block.BlockModel;
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
        System.out.println("GroupBlocksCommand.execute()");
        Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
        // do not execute if selected blocks is less than two
        if (selectedBlockControllers.size() < 2) {
            return;
        }
        List<BlockModel> selectedBlockModels = new ArrayList<>();
        for (BlockController blockController : selectedBlockControllers) {
            selectedBlockModels.add(blockController.getModel());
        }
        BlockGroupModel blockGroupModel = new BlockGroupModel(workspaceModel.getBlockGroupIndex());
        blockGroupModel.setBlocks(selectedBlockModels);
        workspaceModel.addBlockGroupModel(blockGroupModel);

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

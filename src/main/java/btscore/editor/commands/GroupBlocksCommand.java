package btscore.editor.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import btscore.graph.block.BlockController;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;

/**
 *
 * @author Joost
 */
public class GroupBlocksCommand implements UndoableCommand {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private final Collection<BlockController> blocks;
    private BlockGroupModel group;

    public GroupBlocksCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.blocks = workspaceController.getSelectedBlockControllers();
    }

    @Override
    public boolean execute() {
        System.out.println("GroupBlocksCommand.execute()");
        // do not execute if selected blocks is less than two
        if (blocks.size() < 2) {
            return false;
        }
        List<BlockModel> blockModels = new ArrayList<>();
        for (BlockController blockController : blocks) {
            blockModels.add(blockController.getModel());
        }

        if (group == null) { // Create the group, because it was not yet created
            group = new BlockGroupModel(workspaceModel.getBlockGroupIndex());

        } else { // Revive the group, because it was removed through undo
            group.revive();
        }
        group.setBlocks(blockModels);
        workspaceModel.addBlockGroupModel(group);
        return true;
    }

    @Override
    public void undo() {
        if (group != null) { // Selected number of blocks was more than one, TODO this command should NOT have been recorded
            workspaceModel.removeBlockGroupModel(group);
        }
    }
}

package btscore.context.command;

import java.util.ArrayList;
import java.util.List;
import btscore.graph.block.BlockModel;
import btscore.graph.group.BlockGroupModel;
import btscore.workspace.WorkspaceModel;
import btscore.context.UndoableCommand;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements UndoableCommand {

    private final WorkspaceModel workspaceModel;
    private final BlockGroupModel group;
    private final List<BlockModel> blocks;

    public RemoveGroupCommand(WorkspaceModel workspaceModel, BlockGroupModel group) {
        this.workspaceModel = workspaceModel;
        this.group = group;
        this.blocks = new ArrayList<>(group.getBlocks());
    }

    @Override
    public boolean execute() {
        workspaceModel.removeBlockGroupModel(group);
        return true;

    }

    @Override
    public void undo() {
        group.revive();
        group.setBlocks(blocks);
        workspaceModel.addBlockGroupModel(group);
    }
}

package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockModel;
import vplcore.graph.group.BlockGroupModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements Undoable {

    private final WorkspaceModel workspaceModel;
    private final BlockGroupModel group;
    private final List<BlockModel> blocks;

    public RemoveGroupCommand(WorkspaceModel workspaceModel, BlockGroupModel group) {
        this.workspaceModel = workspaceModel;
        this.group = group;
        this.blocks = new ArrayList<>(group.getBlocks());
    }

    @Override
    public void execute() {
        workspaceModel.removeBlockGroupModel(group);
    }

    @Override
    public void undo() {
        group.revive();
        group.setBlocks(blocks);
        workspaceModel.addBlockGroupModel(group);
    }
}

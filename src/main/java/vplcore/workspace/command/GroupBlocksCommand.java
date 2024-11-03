package vplcore.workspace.command;

import vplcore.graph.model.BlockGroup;
import vplcore.workspace.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class GroupBlocksCommand implements Undoable {

    private final WorkspaceController workspace;

    public GroupBlocksCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        if (workspace.blocksSelectedOnWorkspace.size() <= 1) {
            return;
        }
        BlockGroup blockGroup = new BlockGroup(workspace);
        blockGroup.setChildBlocks(workspace.blocksSelectedOnWorkspace);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

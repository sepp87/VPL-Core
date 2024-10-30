package vplcore.workspace.command;

import vplcore.graph.model.BlockGroup;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class GroupBlocksCommand implements Command {

    private final Workspace workspace;

    public GroupBlocksCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        if (workspace.selectedBlockSet.size() <= 1) {
            return;
        }
        BlockGroup blockGroup = new BlockGroup(workspace);
        blockGroup.setChildBlocks(workspace.selectedBlockSet);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

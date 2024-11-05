package vplcore.context.command;

import vplcore.graph.model.Block;
import vplcore.workspace.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class SelectAllBlocksCommand implements Command {

    private final WorkspaceController workspace;

    public SelectAllBlocksCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.blocksSelectedOnWorkspace.clear();
        for (Block block : workspace.blocksOnWorkspace) {
            block.setSelected(true);
            workspace.blocksSelectedOnWorkspace.add(block);
        }
    }


}

package btscore.context.command;

import btscore.context.Command;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class DeselectAllBlocksCommand implements Command {

    private final WorkspaceController workspace;

    public DeselectAllBlocksCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        workspace.deselectAllBlocks();
        return true;
    }

}

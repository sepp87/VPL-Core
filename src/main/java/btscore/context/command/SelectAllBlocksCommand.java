package btscore.context.command;

import btscore.context.Command;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class SelectAllBlocksCommand implements Command {

    private final WorkspaceController workspaceController;

    public SelectAllBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public boolean execute() {
        workspaceController.selectAllBlocks();
        return true;
    }


}

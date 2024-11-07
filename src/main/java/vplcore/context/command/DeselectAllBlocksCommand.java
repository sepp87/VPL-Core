package vplcore.context.command;

import vplcore.context.Command;
import vplcore.workspace.WorkspaceController;

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
    public void execute() {
        workspace.deselectAllBlocks();
    }


}

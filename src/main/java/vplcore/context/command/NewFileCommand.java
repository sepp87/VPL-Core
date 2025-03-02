package vplcore.context.command;

import vplcore.context.ResetHistoryCommand;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements ResetHistoryCommand {

    private final WorkspaceController workspace;

    public NewFileCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        workspace.reset();
        return true;

    }

}

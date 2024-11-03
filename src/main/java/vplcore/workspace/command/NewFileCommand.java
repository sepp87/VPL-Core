package vplcore.workspace.command;

import vplcore.workspace.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements Command {

    private final WorkspaceController workspace;

    public NewFileCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.reset();
    }

}

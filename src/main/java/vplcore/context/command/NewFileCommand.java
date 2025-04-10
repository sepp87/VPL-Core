package vplcore.context.command;

import vplcore.context.Command;
import vplcore.context.ResetHistoryCommand;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements Command, ResetHistoryCommand {

    private final WorkspaceModel workspaceModel;

    public NewFileCommand(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {
        workspaceModel.reset();
        return true;

    }

}

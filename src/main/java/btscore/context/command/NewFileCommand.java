package btscore.context.command;

import btscore.context.Command;
import btscore.context.ResetHistoryCommand;
import btscore.workspace.WorkspaceModel;

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

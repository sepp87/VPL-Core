package btscore.editor.commands;

import btscore.editor.context.Command;
import btscore.editor.context.ResetHistoryCommand;
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

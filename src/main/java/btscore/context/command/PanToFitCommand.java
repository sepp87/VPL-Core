package btscore.context.command;

import btscore.context.Command;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class PanToFitCommand implements Command {

    private final WorkspaceController workspace;

    public PanToFitCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        return true;
    }

}

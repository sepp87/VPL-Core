package btscore.context.command;

import btscore.context.Command;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomToFitCommand implements Command {

    private final WorkspaceController workspace;

    public ZoomToFitCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        workspace.zoomToFit();
        return true;
    }



}

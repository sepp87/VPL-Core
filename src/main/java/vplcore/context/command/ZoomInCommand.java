package vplcore.context.command;

import vplcore.context.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ZoomInCommand implements Command {

    private final WorkspaceController workspace;

    public ZoomInCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public boolean execute() {
        workspace.zoomIn();
        return true;
    }



}

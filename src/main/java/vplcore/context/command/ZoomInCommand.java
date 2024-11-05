package vplcore.context.command;

import vplcore.workspace.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class ZoomInCommand implements Command {

    private final WorkspaceController workspace;

    public ZoomInCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.zoomIn();
    }



}

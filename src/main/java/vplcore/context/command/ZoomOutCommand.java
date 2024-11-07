package vplcore.context.command;

import vplcore.context.Command;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class ZoomOutCommand implements Command {

    private final WorkspaceController workspace;

    public ZoomOutCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.zoomOut();
    }


}

package vplcore.workspace.command;

import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class ZoomInCommand implements Command {

    private final Workspace workspace;

    public ZoomInCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.zoomIn();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

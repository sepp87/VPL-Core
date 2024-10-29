package vplcore.workspace.command;

import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class ZoomToFitCommand implements Command {

    private final Workspace workspace;

    public ZoomToFitCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

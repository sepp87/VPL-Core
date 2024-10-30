package vplcore.workspace.command;

import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class NewFileCommand implements Command {

    private final Workspace workspace;

    public NewFileCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.reset();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

package vplcore.context.command;

import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.workspace.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class CreateConnectionCommand implements Undoable {

    private final WorkspaceController workspaceController;
    private final Port startPort;
    private final Port endPort;

    public CreateConnectionCommand(WorkspaceController workspaceController, Port startPort, Port endPort) {
        this.workspaceController = workspaceController;
        this.startPort = startPort;
        this.endPort = endPort;
    }

    @Override
    public void execute() {
        workspaceController.addConnection(startPort, endPort);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

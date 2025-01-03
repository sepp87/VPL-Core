package vplcore.context.command;

import vplcore.graph.model.Port;
import vplcore.context.Undoable;
import vplcore.workspace.PortModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * TODO Not yet implemented
 * 
 * @author Joost
 */
public class CreateConnectionCommand implements Undoable {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private Port startPort;
    private Port endPort;

    private PortModel startPortModel;
    private PortModel endPortModel;

    public CreateConnectionCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel, Port startPort, Port endPort) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.startPort = startPort;
        this.endPort = endPort;
    }

    public CreateConnectionCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel, PortModel startPort, PortModel endPort) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.startPortModel = startPort;
        this.endPortModel = endPort;
    }

    @Override
    public void execute() {
        if (vplcore.App.BLOCK_MVC) {
            workspaceModel.addConnectionModel(startPortModel, endPortModel);
        } else {
            workspaceController.addConnection(startPort, endPort);
        }

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

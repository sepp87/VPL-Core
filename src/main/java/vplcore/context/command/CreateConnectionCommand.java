package vplcore.context.command;

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

    private PortModel startPortModel;
    private PortModel endPortModel;

    public CreateConnectionCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel, PortModel startPort, PortModel endPort) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.startPortModel = startPort;
        this.endPortModel = endPort;
    }

    @Override
    public void execute() {
        workspaceModel.addConnectionModel(startPortModel, endPortModel);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

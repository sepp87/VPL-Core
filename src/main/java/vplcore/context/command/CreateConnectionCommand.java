package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
import vplcore.context.Undoable;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * TODO Not yet implemented
 *
 * @author Joost
 */
public class CreateConnectionCommand implements Undoable {

    private final WorkspaceModel workspaceModel;
    private final PortModel startPortModel;
    private final PortModel endPortModel;
    private List<ConnectionModel> removedConnections;

    public CreateConnectionCommand(WorkspaceModel workspaceModel, PortModel startPort, PortModel endPort) {
        this.workspaceModel = workspaceModel;
        this.startPortModel = startPort;
        this.endPortModel = endPort;
    }

    @Override
    public void execute() {
        System.out.println("CreateConnectionCommand.execute()");

//        if (!endPortModel.isMultiDockAllowed()) {
//            removedConnections = new ArrayList<>(endPortModel.getConnections());
//            for (ConnectionModel connection : removedConnections) {
//                workspaceModel.removeConnectionModel(connection);
//            }
//        }
        workspaceModel.addConnectionModel(startPortModel, endPortModel);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

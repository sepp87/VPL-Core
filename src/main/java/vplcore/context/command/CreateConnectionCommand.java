package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;

/**
 *
 * TODO Not yet implemented
 *
 * @author Joost
 */
public class CreateConnectionCommand implements UndoableCommand {

    private final WorkspaceModel workspaceModel;
    private final PortModel startPortModel;
    private final PortModel endPortModel;
    private final List<ConnectionModel> removedConnections;
    private ConnectionModel newConnection;

    public CreateConnectionCommand(WorkspaceModel workspaceModel, PortModel startPort, PortModel endPort) {
        this.workspaceModel = workspaceModel;
        this.startPortModel = startPort;
        this.endPortModel = endPort;
        this.removedConnections = new ArrayList<>();
    }

    @Override
    public boolean execute() {
        System.out.println("CreateConnectionCommand.execute()");
        if (!endPortModel.isMultiDockAllowed()) { // remove all connections for the receiving (INPUT) port if multi dock is NOT allowed
            Set<ConnectionModel> connections = endPortModel.getConnections();
            for (ConnectionModel connection : connections) {
                workspaceModel.removeConnectionModel(connection);
            }
            removedConnections.addAll(connections);
        }

        if (newConnection == null) { // create the new connection
            newConnection = workspaceModel.addConnectionModel(startPortModel, endPortModel);
        } else { // revive the connection, because it was undone
            newConnection.revive();
            workspaceModel.addConnectionModel(newConnection);
        }
        return true;
    }

    @Override
    public void undo() {
        workspaceModel.removeConnectionModel(newConnection);
        for (ConnectionModel connection : removedConnections) {
            connection.revive();
            workspaceModel.addConnectionModel(connection);
        }
    }
}

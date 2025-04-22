package btscore.editor.commands;

import btscore.graph.connection.ConnectionModel;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.graph.port.PortModel;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements UndoableCommand {

    private final WorkspaceModel workspaceModel;
    private final ConnectionModel connection;
    private ConnectionModel autoConnection;

    public RemoveConnectionCommand(WorkspaceModel workspaceModel, ConnectionModel connection) {
        this.workspaceModel = workspaceModel;
        this.connection = connection;
    }

    @Override
    public boolean execute() {
        workspaceModel.removeConnectionModel(connection);
        if (connection.getEndPort().wirelessProperty().get()) {
            PortModel port = connection.getEndPort();
            ConnectionModel newConnection = workspaceModel.getWirelessIndex().registerReceiver(port);
            if(newConnection != null) {
                this.autoConnection = newConnection;
                workspaceModel.addConnectionModel(newConnection);
            }
        }
        return true;

    }

    @Override
    public void undo() {
        if(autoConnection != null) {
            workspaceModel.removeConnectionModel(autoConnection);
            autoConnection = null;
        }
        connection.revive();
        workspaceModel.addConnectionModel(connection);
    }
}

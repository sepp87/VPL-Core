package vplcore.context.command;

import vplcore.graph.connection.ConnectionModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements UndoableCommand {

    private final WorkspaceModel workspaceModel;
    private final ConnectionModel connection;

    public RemoveConnectionCommand(WorkspaceModel workspaceModel, ConnectionModel connection) {
        this.workspaceModel = workspaceModel;
        this.connection = connection;
    }

    @Override
    public boolean execute() {
        workspaceModel.removeConnectionModel(connection);
        return true;

    }

    @Override
    public void undo() {
        connection.revive();
        workspaceModel.addConnectionModel(connection);
    }
}

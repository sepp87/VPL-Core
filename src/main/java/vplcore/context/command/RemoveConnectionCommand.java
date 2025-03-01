package vplcore.context.command;

import vplcore.context.Undoable;
import vplcore.graph.connection.ConnectionModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements Undoable {

    private final WorkspaceModel workspaceModel;
    private final ConnectionModel connection;

    public RemoveConnectionCommand(WorkspaceModel workspaceModel, ConnectionModel connection) {
        this.workspaceModel = workspaceModel;
        this.connection = connection;
    }

    @Override
    public void execute() {
        workspaceModel.removeConnectionModel(connection);
    }

    @Override
    public void undo() {
        connection.revive();
        workspaceModel.addConnectionModel(connection);
    }
}

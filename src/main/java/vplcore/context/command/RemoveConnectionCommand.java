package vplcore.context.command;

import vplcore.context.Undoable;
import vplcore.workspace.ConnectionModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class RemoveConnectionCommand implements Undoable {

    private final WorkspaceModel workspaceModel;
    private final ConnectionModel connectionModel;

    public RemoveConnectionCommand(WorkspaceModel workspaceModel, ConnectionModel connectionModel) {
        this.workspaceModel = workspaceModel;
        this.connectionModel = connectionModel;
    }

    @Override
    public void execute() {
        workspaceModel.removeConnectionModel(connectionModel);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

package vplcore.context.command;

import vplcore.graph.model.BlockGroup;
import vplcore.context.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class GroupBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;

    public GroupBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public void execute() {
        workspaceController.addBlockGroup();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

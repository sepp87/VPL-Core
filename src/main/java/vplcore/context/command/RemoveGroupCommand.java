package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.BlockGroupModel;
import vplcore.workspace.BlockModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class RemoveGroupCommand implements Undoable {

    private final WorkspaceModel workspaceModel;
    private final BlockGroupModel blockGroupModel;

    public RemoveGroupCommand(WorkspaceModel workspaceModel, BlockGroupModel blockGroupModel) {
        this.workspaceModel = workspaceModel;
        this.blockGroupModel = blockGroupModel;
    }

    @Override
    public void execute() {
        if (vplcore.App.BLOCK_MVC) {
            workspaceModel.removeBlockGroupModel(blockGroupModel);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

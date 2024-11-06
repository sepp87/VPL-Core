package vplcore.context.command;

import java.util.Collection;
import vplcore.graph.model.Block;
import vplcore.workspace.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class DeleteSelectedBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;

    public DeleteSelectedBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public void execute() {
        Collection<Block> selectedBlocks = workspaceController.getSelectedBlocks();
        workspaceController.deselectAllBlocks();
        for (Block block : selectedBlocks) {
            block.delete();
        }
//
//        for (Block block : workspaceController.blocksSelectedOnWorkspace) {
//            block.delete();
//        }
//        workspaceController.blocksSelectedOnWorkspace.clear();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

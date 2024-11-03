package vplcore.workspace.command;

import vplcore.graph.model.Block;
import vplcore.workspace.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class DeleteSelectedBlocksCommand implements Undoable {

    private final WorkspaceController workspace;

    public DeleteSelectedBlocksCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        for (Block block : workspace.blocksSelectedOnWorkspace) {
            block.delete();
        }
        workspace.blocksSelectedOnWorkspace.clear();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

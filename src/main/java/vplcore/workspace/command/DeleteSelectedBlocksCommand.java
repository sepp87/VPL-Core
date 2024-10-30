package vplcore.workspace.command;

import vplcore.graph.model.Block;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class DeleteSelectedBlocksCommand implements Command {

    private final Workspace workspace;

    public DeleteSelectedBlocksCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        for (Block block : workspace.selectedBlockSet) {
            block.delete();
        }
        workspace.selectedBlockSet.clear();
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

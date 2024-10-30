package vplcore.workspace.command;

import vplcore.graph.model.Block;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class SelectAllBlocksCommand implements Command {

    private final Workspace workspace;

    public SelectAllBlocksCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.selectedBlockSet.clear();
        for (Block block : workspace.blockSet) {
            block.setSelected(true);
            workspace.selectedBlockSet.add(block);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

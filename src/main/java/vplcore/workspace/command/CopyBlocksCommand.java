package vplcore.workspace.command;

import javafx.collections.FXCollections;
import vplcore.graph.model.Block;
import vplcore.workspace.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class CopyBlocksCommand implements Undoable {

    private final WorkspaceController workspace;

    public CopyBlocksCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.blocksCopied = FXCollections.observableSet();
        for (Block block : workspace.blocksSelectedOnWorkspace) {
            workspace.blocksCopied.add(block);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

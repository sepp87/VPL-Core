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

    private final WorkspaceController workspaceController;

    public CopyBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public void execute() {
        workspaceController.blocksCopied = FXCollections.observableSet();
        for (Block block : workspaceController.blocksSelectedOnWorkspace) {
            workspaceController.blocksCopied.add(block);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

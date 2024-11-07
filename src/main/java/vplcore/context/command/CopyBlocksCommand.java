package vplcore.context.command;

import java.util.Collection;
import vplcore.context.CopyPasteMemory;
import vplcore.graph.model.Block;
import vplcore.context.Undoable;
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
        Collection<Block> selectedBlocks = workspaceController.getSelectedBlocks();
        CopyPasteMemory.save(selectedBlocks);
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}

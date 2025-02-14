package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.context.CopyPasteMemory;
import vplcore.graph.model.Block;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.BlockModel;
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
        if (vplcore.App.BLOCK_MVC) {
            Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
            List<BlockModel> selectedBlockModels = new ArrayList<>();
            for (BlockController blockController : selectedBlockControllers) {
                selectedBlockModels.add(blockController.getModel());
            }
            CopyPasteMemory.saveBlockModels(workspaceController, selectedBlockModels);
        } else {
            Collection<Block> selectedBlocks = workspaceController.getSelectedBlocks();
            CopyPasteMemory.save(selectedBlocks);
        }

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

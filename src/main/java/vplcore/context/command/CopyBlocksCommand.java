package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.context.CopyPasteMemory;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
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
        Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
        List<BlockModel> selectedBlockModels = new ArrayList<>();
        for (BlockController blockController : selectedBlockControllers) {
            selectedBlockModels.add(blockController.getModel());
        }
        CopyPasteMemory.saveBlockModels(workspaceController, selectedBlockModels);

    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}

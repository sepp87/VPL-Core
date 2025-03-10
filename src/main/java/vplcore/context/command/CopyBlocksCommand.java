package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.context.Command;
import vplcore.context.CopyPasteMemory;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyBlocksCommand implements Command {

    private final WorkspaceController workspaceController;

    public CopyBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
    }

    @Override
    public boolean execute() {
        Collection<BlockController> selectedBlockControllers = workspaceController.getSelectedBlockControllers();
        List<BlockModel> selectedBlockModels = new ArrayList<>();
        for (BlockController blockController : selectedBlockControllers) {
            selectedBlockModels.add(blockController.getModel());
        }
        CopyPasteMemory.saveBlockModels(workspaceController, selectedBlockModels);
        return true;
    }

}

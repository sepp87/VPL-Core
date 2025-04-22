package btscore.editor.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import btscore.editor.context.Command;
import btscore.clipboard.CopyPasteMemory;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceController;

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

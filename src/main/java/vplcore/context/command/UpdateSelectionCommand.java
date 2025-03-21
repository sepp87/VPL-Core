package vplcore.context.command;

import vplcore.context.Command;
import vplcore.graph.block.BlockController;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class UpdateSelectionCommand implements Command {

    private final WorkspaceController workspaceController;
    private final BlockController blockController;
    private final boolean isModifierDown;

    public UpdateSelectionCommand(WorkspaceController workspaceController, BlockController blockController, boolean isModifierDown) {
        this.workspaceController = workspaceController;
        this.blockController = blockController;
        this.isModifierDown = isModifierDown;
    }

    @Override
    public boolean execute() {
        workspaceController.updateSelection(blockController, isModifierDown);
        return true;
    }


}

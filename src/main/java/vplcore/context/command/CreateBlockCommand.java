package vplcore.context.command;

import javafx.geometry.Point2D;
import vplcore.graph.util.BlockFactory;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;

/**
 *
 * @author Joost
 */
public class CreateBlockCommand implements UndoableCommand {

    private final String blockIdentifier;
    private final Point2D location;
    private final WorkspaceModel workspaceModel;
    private BlockModel blockModel;

    public CreateBlockCommand(WorkspaceModel workspaceModel, String blockIdentifier, Point2D location) {
        this.workspaceModel = workspaceModel;
        this.blockIdentifier = blockIdentifier;
        this.location = location;
    }

    @Override
    public boolean execute() {
        if (blockModel == null) {
            blockModel = BlockFactory.createBlock(blockIdentifier, workspaceModel);
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
        } else {
            blockModel.revive();
        }
        workspaceModel.addBlockModel(blockModel);
        return true;
    }

    @Override
    public void undo() {
        workspaceModel.removeBlockModel(blockModel);
    }

}

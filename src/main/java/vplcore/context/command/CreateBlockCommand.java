package vplcore.context.command;

import javafx.geometry.Point2D;
import vplcore.context.Undoable;
import vplcore.graph.util.BlockModelFactory;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class CreateBlockCommand implements Undoable {

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
    public void execute() {
        if (blockModel == null) {
            blockModel = BlockModelFactory.createBlock(blockIdentifier, workspaceModel);
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
        } else {
            blockModel.revive();
        }
        workspaceModel.addBlockModel(blockModel);
    }

    @Override
    public void undo() {
        workspaceModel.removeBlockModel(blockModel);
    }

}

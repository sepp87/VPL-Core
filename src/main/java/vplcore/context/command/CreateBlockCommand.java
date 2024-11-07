package vplcore.context.command;

import javafx.geometry.Point2D;
import vplcore.graph.model.Block;
import vplcore.graph.util.BlockFactory;
import vplcore.context.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class CreateBlockCommand implements Undoable {

    private final String blockIdentifier;
    private final Point2D location;
    private final WorkspaceController workspaceController;
    private Block block;

    public CreateBlockCommand(WorkspaceController workspaceController, String blockIdentifier, Point2D location) {
        this.workspaceController = workspaceController;
        this.blockIdentifier = blockIdentifier;
        this.location = workspaceController.getView().sceneToLocal(location);

    }

    @Override
    public void execute() {
        this.block = BlockFactory.createBlock(blockIdentifier, workspaceController);
        block.setLayoutX(location.getX());
        block.setLayoutY(location.getY());
        workspaceController.addBlock(block);
    }

    @Override
    public void undo() {
//        workspaceController.removeChild(block);
    }

}

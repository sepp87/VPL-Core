package vplcore.context.command;

import javafx.geometry.Point2D;
import vplcore.graph.model.Block;
import vplcore.graph.util.BlockFactory;
import vplcore.context.Undoable;
import vplcore.graph.util.BlockModelFactory;
import vplcore.workspace.BlockModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class CreateBlockCommand implements Undoable {

    private final String blockIdentifier;
    private final Point2D location;
    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private Block block;
    private BlockModel blockModel;

    public CreateBlockCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel, String blockIdentifier, Point2D location) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
        this.blockIdentifier = blockIdentifier;
        this.location = workspaceController.getView().sceneToLocal(location);
    }

    @Override
    public void execute() {
        if (vplcore.App.BLOCK_MVC) {
            this.blockModel = BlockModelFactory.createBlock(blockIdentifier, workspaceModel);
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
            workspaceModel.addBlockModel(blockModel);
        } else {
            this.block = BlockFactory.createBlock(blockIdentifier, workspaceController);
            block.setLayoutX(location.getX());
            block.setLayoutY(location.getY());
            workspaceController.addBlock(block);
        }

    }

    @Override
    public void undo() {
//        workspaceController.removeChild(block);
    }

}

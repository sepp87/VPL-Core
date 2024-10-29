package vplcore.workspace.command;

import javafx.geometry.Point2D;
import vplcore.graph.model.Block;
import vplcore.graph.util.BlockFactory;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class CreateBlockCommand implements Command {

    private final String blockIdentifier;
    private final Point2D location;
    private final Workspace workspace;
    private Block block;

    public CreateBlockCommand(String blockIdentifier, Point2D location, Workspace workspace) {
        this.blockIdentifier = blockIdentifier;
        this.location = workspace.sceneToLocal(location);
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        this.block = BlockFactory.createBlock(blockIdentifier, workspace);
        block.setLayoutX(location.getX());
        block.setLayoutY(location.getY());
        workspace.getChildren().add(block);
        workspace.blockSet.add(block);
    }

    @Override
    public void undo() {
        workspace.getChildren().remove(block);
        workspace.blockSet.remove(block);
    }

}

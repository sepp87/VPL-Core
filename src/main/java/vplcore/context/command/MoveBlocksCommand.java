package vplcore.context.command;

import java.util.Collection;
import javafx.geometry.Point2D;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;

/**
 *
 * @author Joost
 */
public class MoveBlocksCommand implements Undoable {

    private final Collection<BlockController> blockControllers;
    private final Point2D delta;

    public MoveBlocksCommand(Collection<BlockController> blockControllers, Point2D delta) {
        this.blockControllers = blockControllers;
        this.delta = delta;
    }

    @Override
    public void execute() {
        // command is only executed to register the move, so it can be undone
    }

    @Override
    public void undo() {
        for (BlockController blockController : blockControllers) {
            BlockModel blockModel = blockController.getModel();
            double x = blockModel.layoutXProperty().get();
            double y = blockModel.layoutYProperty().get();
            blockModel.layoutXProperty().set(x - delta.getX());
            blockModel.layoutYProperty().set(y - delta.getY());
        }
    }

}

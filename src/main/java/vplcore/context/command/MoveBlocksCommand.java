package vplcore.context.command;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Point2D;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class MoveBlocksCommand implements Undoable {

    private final Collection<BlockController> blocks;
    private final Point2D delta;
    private final Map<String, Point2D> previousLocations = new TreeMap<>();
    private final Map<String, Point2D> currentLocations = new TreeMap<>();

    public MoveBlocksCommand(Collection<BlockController> blocks, Point2D delta) {
        this.blocks = blocks;
        this.delta = delta;
        saveLocations();
    }

    private void saveLocations() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            double x = blockModel.layoutXProperty().get();
            double y = blockModel.layoutYProperty().get();
            Point2D previousLocation = new Point2D(x - delta.getX(), y - delta.getY());
            Point2D currentLocation = new Point2D(x, y);
            previousLocations.put(blockModel.getId(), previousLocation);
            currentLocations.put(blockModel.getId(), currentLocation);
        }
    }

    @Override
    public void execute() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            Point2D location = currentLocations.get(blockModel.getId());
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
        }

    }

    @Override
    public void undo() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            Point2D location = previousLocations.get(blockModel.getId());
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
        }
    }

}

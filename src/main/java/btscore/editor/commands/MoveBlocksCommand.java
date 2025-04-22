package btscore.editor.commands;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Point2D;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockModel;
import btscore.editor.context.UndoableCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class MoveBlocksCommand implements UndoableCommand {

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
    public boolean execute() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            Point2D location = currentLocations.get(blockModel.getId());
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
        }
        return true;
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

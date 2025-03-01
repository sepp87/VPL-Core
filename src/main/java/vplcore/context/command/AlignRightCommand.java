package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Bounds;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockView;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignRightCommand implements Undoable {

    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignRightCommand(WorkspaceController workspace) {
        this.blocks = workspace.getSelectedBlockControllers();
    }

    @Override
    public void execute() {
        List<BlockView> blockViews = new ArrayList<>();
        for (BlockController blockController : blocks) {
            blockViews.add(blockController.getView());
        }
        Bounds bBox = BlockView.getBoundingBoxOfBlocks(blockViews);
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            BlockView blockView = blockController.getView();
            previousLocations.put(blockModel.getId(), blockModel.layoutXProperty().get());
            blockModel.layoutXProperty().set(bBox.getMaxX() - blockView.getWidth());
        }

    }

    @Override
    public void undo() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            blockModel.layoutXProperty().set(previousLocations.get(blockModel.getId()));
        }
    }
}

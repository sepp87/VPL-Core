package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Bounds;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockView;
import vplcore.workspace.WorkspaceController;
import vplcore.context.UndoableCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignVerticallyCommand implements UndoableCommand {

    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignVerticallyCommand(WorkspaceController workspace) {
        this.blocks = workspace.getSelectedBlockControllers();
    }

    @Override
    public boolean execute() {
        List<BlockView> blockViews = new ArrayList<>();
        for (BlockController blockController : blocks) {
            blockViews.add(blockController.getView());
        }
        Bounds bBox = BlockView.getBoundingBoxOfBlocks(blockViews);
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            BlockView blockView = blockController.getView();
            previousLocations.put(blockModel.getId(), blockModel.layoutXProperty().get());
            blockModel.layoutXProperty().set(bBox.getMaxX() - bBox.getWidth() / 2 - blockView.getWidth() / 2);
        }
        return true;
    }

    @Override
    public void undo() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            blockModel.layoutXProperty().set(previousLocations.get(blockModel.getId()));
        }
    }
}

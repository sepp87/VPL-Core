package btscore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javafx.geometry.Bounds;
import btscore.graph.block.BlockController;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;
import btscore.workspace.WorkspaceController;
import btscore.context.UndoableCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignBottomCommand implements UndoableCommand {

    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignBottomCommand(WorkspaceController workspace) {

        this.blocks = workspace.getSelectedBlockControllers();
        System.out.println(blocks.size() + " number of blocks");
    }

    @Override
    public boolean execute() {
        List<BlockView> blockViews = new ArrayList<>();
        for (BlockController blockController : blocks) {
            blockViews.add(blockController.getView());
        }
        Bounds bBox = BlockView.getBoundingBoxOfBlocks(blockViews);
        System.out.println("Bottom");
        for (BlockController blockController : blocks) {
            System.out.println("Bottom 2");
            BlockModel blockModel = blockController.getModel();
            BlockView blockView = blockController.getView();
            previousLocations.put(blockModel.getId(), blockModel.layoutYProperty().get());
            blockModel.layoutYProperty().set(bBox.getMaxY() - blockView.getHeight());
        }
        return true;
    }

    @Override
    public void undo() {
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            blockModel.layoutYProperty().set(previousLocations.get(blockModel.getId()));
        }
    }
}

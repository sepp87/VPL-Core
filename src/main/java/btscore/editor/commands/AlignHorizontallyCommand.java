package btscore.editor.commands;

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
import btscore.editor.context.UndoableCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlignHorizontallyCommand implements UndoableCommand {

    private final Collection<BlockController> blocks;
    private final Map<String, Double> previousLocations = new TreeMap<>();

    public AlignHorizontallyCommand(WorkspaceController workspace) {
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
            previousLocations.put(blockModel.getId(), blockModel.layoutYProperty().get());
            blockModel.layoutYProperty().set(bBox.getMaxY() - bBox.getHeight() / 2 - blockView.getHeight() / 2);
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

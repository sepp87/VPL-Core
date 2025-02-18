package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import vplcore.context.Undoable;
import vplcore.workspace.BlockController;
import vplcore.workspace.BlockModel;
import vplcore.workspace.BlockView;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class AlignHorizontallyCommand implements Undoable {

    private final WorkspaceController workspace;

    public AlignHorizontallyCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        List<BlockView> blockViews = new ArrayList<>();
        for (BlockController blockController : workspace.getSelectedBlockControllers()) {
            blockViews.add(blockController.getView());
        }
        Bounds bBox = BlockView.getBoundingBoxOfBlocks(blockViews);
        for (BlockController blockController : workspace.getSelectedBlockControllers()) {
            BlockModel blockModel = blockController.getModel();
            BlockView blockView = blockController.getView();
            blockModel.layoutYProperty().set(bBox.getMaxY() - bBox.getHeight() / 2 - blockView.getHeight() / 2);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

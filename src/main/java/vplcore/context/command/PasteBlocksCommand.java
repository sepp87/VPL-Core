package vplcore.context.command;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import vplcore.App;
import vplcore.context.CopyPasteMemory;
import vplcore.context.CopyResult;
import vplcore.context.Undoable;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class PasteBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;

    public PasteBlocksCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
    }

    @Override
    public void execute() {

        if (!CopyPasteMemory.containsItems()) {
            return;
        }
        CopyResult copy = CopyPasteMemory.getCopyResult();
        Bounds boundingBox = copy.boundingBox;

        if (boundingBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(boundingBox.getMinX() + boundingBox.getWidth() / 2, boundingBox.getMinY() + boundingBox.getHeight() / 2);
        Point2D pastePoint = App.getContext(workspaceController.getContextId()).getMousePositionOnWorkspace();

        Point2D delta = pastePoint.subtract(copyPoint);

        // First deselect selected blocks.
        workspaceController.deselectAllBlocks();

        // Paste blocks to workspace and set them selected
        for (BlockModel copiedBlock : copy.blockModels) {
            copiedBlock.layoutXProperty().set(copiedBlock.layoutXProperty().get() + delta.getX());
            copiedBlock.layoutYProperty().set(copiedBlock.layoutYProperty().get() + delta.getY());
            workspaceModel.addBlockModel(copiedBlock);
        }

        // Select newly created blocks 
        // TODO double check if controllers are actually already created at this point
        for (BlockModel copiedBlock : copy.blockModels) {
            workspaceController.selectBlock(copiedBlock);
        }

        for (ConnectionModel copiedConnection : copy.connectionModels) {
            workspaceModel.addConnectionModel(copiedConnection);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

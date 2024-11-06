package vplcore.context.command;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import vplcore.App;
import vplcore.context.CopyPasteMemory;
import vplcore.context.CopyResult;
import vplcore.graph.model.Block;
import vplcore.graph.model.Connection;
import vplcore.workspace.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class PasteBlocksCommand implements Undoable {

    private final WorkspaceController workspaceController;

    public PasteBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
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
        for (Block copiedBlock : copy.blocks) {
            copiedBlock.setLayoutX(copiedBlock.getLayoutX() + delta.getX());
            copiedBlock.setLayoutY(copiedBlock.getLayoutY() + delta.getY());
            copiedBlock.setSelected(true);
            workspaceController.addBlock(copiedBlock);
        }

        for (Connection copiedConnection : copy.connections) {
            workspaceController.addConnection(copiedConnection);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

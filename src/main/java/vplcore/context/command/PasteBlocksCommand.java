package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import vplcore.App;
import vplcore.context.CopyPasteMemory;
import vplcore.context.CopyResult;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class PasteBlocksCommand implements UndoableCommand {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private final List<BlockModel> pastedBlocks = new ArrayList<>();
    private final List<ConnectionModel> pastedConnections = new ArrayList<>();

    public PasteBlocksCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        if (!pastedBlocks.isEmpty()) { // add the pasted blocks and connections again, since they were remove through undo
            for (BlockModel block : pastedBlocks) {
                block.revive();
                workspaceModel.addBlockModel(block);
            }
            for (ConnectionModel connection : pastedConnections) {
                connection.revive();
                workspaceModel.addConnectionModel(connection);
            }
            return true;
        }

        if (!CopyPasteMemory.containsItems()) { // TODO this command should NOT be recorded since there was nothing copied to begin with
            return false;
        }

        // paste all copied blocks and connections, since this command is triggered for the first time
        CopyResult copy = CopyPasteMemory.getCopyResult();
        Bounds boundingBox = copy.boundingBox;

        if (boundingBox == null) {
            System.out.println("PastBlocksCommand.execute() boundingBox == null");
            return false;
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
            pastedBlocks.add(copiedBlock);
        }

        // Select newly created blocks 
        // TODO double check if controllers are actually already created at this point
        for (BlockModel copiedBlock : copy.blockModels) {
            workspaceController.selectBlock(copiedBlock);
        }

        for (ConnectionModel copiedConnection : copy.connectionModels) {
            workspaceModel.addConnectionModel(copiedConnection);
            pastedConnections.add(copiedConnection);
        }
        return true;

    }

    @Override
    public void undo() {

        for (BlockModel block : pastedBlocks) {
            workspaceModel.removeBlockModel(block);
        }

        for (ConnectionModel connection : pastedConnections) {
            workspaceModel.removeConnectionModel(connection);
        }
    }
}

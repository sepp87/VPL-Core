package vplcore.context.command;

import javafx.geometry.Bounds;
import vplcore.graph.model.Block;
import vplcore.workspace.Undoable;
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
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.getSelectedBlocks());
        for (Block block : workspace.getSelectedBlocks()) {
            block.setLayoutY(bBox.getMaxY() - bBox.getHeight() / 2 - block.getHeight());
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

package vplcore.context.command;

import javafx.geometry.Bounds;
import vplcore.graph.model.Block;
import vplcore.context.Undoable;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class AlignTopCommand implements Undoable {

    private final WorkspaceController workspace;

    public AlignTopCommand(WorkspaceController workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.getSelectedBlocks());
        for (Block block : workspace.getSelectedBlocks()) {
            block.setLayoutY(bBox.getMinY());
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

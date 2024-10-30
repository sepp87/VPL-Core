package vplcore.workspace.command;

import javafx.geometry.Bounds;
import vplcore.graph.model.Block;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class AlignRightCommand implements Command {

    private final Workspace workspace;

    public AlignRightCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.selectedBlockSet);
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutX(bBox.getMaxX() - block.getWidth());
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

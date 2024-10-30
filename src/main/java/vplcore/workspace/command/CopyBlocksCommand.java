package vplcore.workspace.command;

import javafx.collections.FXCollections;
import vplcore.graph.model.Block;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class CopyBlocksCommand implements Command {

    private final Workspace workspace;

    public CopyBlocksCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        workspace.tempBlockSet = FXCollections.observableSet();
        for (Block block : workspace.selectedBlockSet) {
            workspace.tempBlockSet.add(block);
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

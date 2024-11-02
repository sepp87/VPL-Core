package vplcore.workspace.command;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import vplcore.graph.model.Block;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.graph.util.CopyConnection;
import vplcore.workspace.Command;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class PasteBlocksCommand implements Command {

    private final Workspace workspace;

    public PasteBlocksCommand(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void execute() {
        if (workspace.tempBlockSet == null || workspace.tempBlockSet.isEmpty()) {
            return;
        }

        Bounds bBox = Block.getBoundingBoxOfBlocks(workspace.tempBlockSet);

        if (bBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(bBox.getMinX() + bBox.getWidth() / 2, bBox.getMinY() + bBox.getHeight() / 2);
        Point2D pastePoint = workspace.mouse.getPosition();

        Point2D delta = pastePoint.subtract(copyPoint);

        //First deselect selected blocks. Simply said, deselect copied blocks.
        for (Block block : workspace.selectedBlockSet) {
            block.setSelected(false);
        }
        workspace.selectedBlockSet.clear();

        List<Connection> alreadyClonedConnectors = new ArrayList<>();
        List<CopyConnection> copyConnections = new ArrayList<>();

        // copy block from clipboard to canvas
        for (Block block : workspace.tempBlockSet) {
            Block newBlock = block.clone();

            newBlock.setLayoutX(block.getLayoutX() + delta.getX());
            newBlock.setLayoutY(block.getLayoutY() + delta.getY());

            workspace.getChildren().add(newBlock);
            workspace.blockSet.add(newBlock);

            //Set pasted block(s) as selected
            workspace.selectedBlockSet.add(newBlock);
            newBlock.setSelected(true);

            copyConnections.add(new CopyConnection(block, newBlock));
        }

        for (CopyConnection cc : copyConnections) {
            int counter = 0;

            for (Port port : cc.oldBlock.inPorts) {
                for (Connection connection : port.connectedConnections) {
                    if (!alreadyClonedConnectors.contains(connection)) {
                        Connection newConnection = null;

                        // start and end block are contained in selection
                        if (workspace.tempBlockSet.contains(connection.getStartPort().parentBlock)) {
                            CopyConnection cc2 = copyConnections
                                    .stream()
                                    .filter(i -> i.oldBlock == connection.getStartPort().parentBlock)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                newConnection = new Connection(workspace, cc2.newBlock.outPorts.get(0), cc.newBlock.inPorts.get(counter));
                            }
                        } else {
                            // only end block is contained in selection
                            newConnection = new Connection(workspace, connection.getStartPort(), cc.newBlock.inPorts.get(counter));
                        }

                        if (newConnection != null) {
                            alreadyClonedConnectors.add(connection);
                            workspace.connectionSet.add(newConnection);
                        }
                    }
                }
                counter++;
            }
        }
    }

    @Override
    public void undo() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}

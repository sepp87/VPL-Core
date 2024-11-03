package vplcore.workspace.command;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import vplcore.graph.model.Block;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.graph.util.CopiedConnection;
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
        if (workspaceController.blocksCopied == null || workspaceController.blocksCopied.isEmpty()) {
            return;
        }

        Bounds bBox = Block.getBoundingBoxOfBlocks(workspaceController.blocksCopied);

        if (bBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(bBox.getMinX() + bBox.getWidth() / 2, bBox.getMinY() + bBox.getHeight() / 2);
        Point2D pastePoint = workspaceController.mouse.getPosition();

        Point2D delta = pastePoint.subtract(copyPoint);

        //First deselect selected blocks. Simply said, deselect copied blocks.
        for (Block block : workspaceController.blocksSelectedOnWorkspace) {
            block.setSelected(false);
        }
        workspaceController.blocksSelectedOnWorkspace.clear();

        List<Connection> alreadyClonedConnectors = new ArrayList<>();
        List<CopiedConnection> copiedConnections = new ArrayList<>();

        // copy block from clipboard to canvas
        for (Block block : workspaceController.blocksCopied) {
            Block newBlock = block.clone();

            newBlock.setLayoutX(block.getLayoutX() + delta.getX());
            newBlock.setLayoutY(block.getLayoutY() + delta.getY());

            workspaceController.addBlock(block);

            //Set pasted block(s) as selected
            workspaceController.blocksSelectedOnWorkspace.add(newBlock);
            newBlock.setSelected(true);

            copiedConnections.add(new CopiedConnection(block, newBlock));
        }

        for (CopiedConnection cc : copiedConnections) {
            int counter = 0;

            for (Port port : cc.oldBlock.inPorts) {
                for (Connection connection : port.connectedConnections) {
                    if (!alreadyClonedConnectors.contains(connection)) {
                        Connection newConnection = null;

                        // start and end block are contained in selection
                        if (workspaceController.blocksCopied.contains(connection.getStartPort().parentBlock)) {
                            CopiedConnection cc2 = copiedConnections
                                    .stream()
                                    .filter(i -> i.oldBlock == connection.getStartPort().parentBlock)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                newConnection = new Connection(workspaceController, cc2.newBlock.outPorts.get(0), cc.newBlock.inPorts.get(counter));
                            }
                        } else {
                            // only end block is contained in selection
                            newConnection = new Connection(workspaceController, connection.getStartPort(), cc.newBlock.inPorts.get(counter));
                        }

                        if (newConnection != null) {
                            alreadyClonedConnectors.add(connection);
                            workspaceController.connectionsOnWorkspace.add(newConnection);
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

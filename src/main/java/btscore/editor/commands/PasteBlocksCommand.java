package btscore.editor.commands;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import btscore.App;
import btscore.clipboard.CopyPasteMemory;
import btscore.clipboard.CopyResult;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.graph.port.PortModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class PasteBlocksCommand implements UndoableCommand {

    private final WorkspaceController workspaceController;
    private final WorkspaceModel workspaceModel;
    private final List<BlockModel> pastedBlocks = new ArrayList<>();
    private final List<ConnectionModel> pastedConnections = new ArrayList<>();
    private final List<ConnectionModel> wirelessConnections = new ArrayList<>();

    public PasteBlocksCommand(WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        this.workspaceController = workspaceController;
        this.workspaceModel = workspaceModel;
    }

    @Override
    public boolean execute() {

        if (pastedBlocks.isEmpty()) { // add the pasted blocks and connections again, since they were remove through undo

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

        } else {
            for (BlockModel block : pastedBlocks) {
                block.revive();
                workspaceModel.addBlockModel(block);
            }
            for (ConnectionModel connection : pastedConnections) {
                connection.revive();
                workspaceModel.addConnectionModel(connection);
            }
        }

        // Register transmitters and receivers. Ignore index since all blocks are new.
        for (BlockModel block : pastedBlocks) {

            // auto-connect transmitters
            List<PortModel> transmitters = block.getTransmittingPorts();
            for (PortModel port : transmitters) {
                List<ConnectionModel> autoConnections = workspaceModel.getAutoConnectIndex().registerTransmitter(port);
                if (!autoConnections.isEmpty()) {
                    /**
                     * Edge case: there can be auto-connections, since the
                     * original copied block with transmitter could have been
                     * removed, when the block is actually pasted onto the
                     * workspace. Steps to reproduce: add transmitter, add
                     * receiver, copy transmitter, delete transmitter, paste
                     * transmitter.
                     */
                    wirelessConnections.addAll(autoConnections);
//                    System.out.println("GENERATING AUTO CONNECTIONS FOR PASTED BLOCK TRANSMITTER");
                }
            }

            // auto-connect receivers that do NOT yet have a connection
            List<PortModel> receivers = block.getReceivingPorts();
            for (PortModel port : receivers) {
                if (port.isConnected()) {
                    continue;
                }
                ConnectionModel autoConnection = workspaceModel.getAutoConnectIndex().registerReceiver(port);
                if (autoConnection != null) {
                    /**
                     * Edge case: there can be an auto-connection, since the
                     * original copied block with receiver could have been
                     * removed, when the block is actually pasted onto the
                     * workspace. Steps to reproduce: add receiver, copy
                     * receiver, add transmitter, paste receiver.
                     */
                    wirelessConnections.add(autoConnection);
//                    System.out.println("GENERATING AUTO CONNECTION FOR PASTED BLOCK RECEIVER");
                }
            }

            // add auto-connections to the workspace 
            for (ConnectionModel connection : wirelessConnections) {
                workspaceModel.addConnectionModel(connection);
            }
        }
        return true;
    }

    /**
     * Info - there is no need to record the index of the transmitters when
     * removing them from the registry, since these newly added blocks are
     * always last in the list
     */
    @Override
    public void undo() {

        // retrieve connected receivers before removing auto-connection, otherwise they are gone
        List<PortModel> connectedReceivers = new ArrayList<>();

        // unregister transmitters and receivers. Ignore index since all blocks are new.
        for (BlockModel block : pastedBlocks) {

            // unregister transmitters
            List<PortModel> transmitters = block.getTransmittingPorts();
            for (PortModel port : transmitters) {
                connectedReceivers.addAll(port.getConnectedPorts());
                workspaceModel.getAutoConnectIndex().unregisterTransmitter(port);
            }

            // unregister receivers
            List<PortModel> receivers = block.getReceivingPorts();
            for (PortModel port : receivers) {
                workspaceModel.getAutoConnectIndex().unregisterReceiver(port);
            }
        }

        // remove auto-connections
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.removeConnectionModel(connection);
        }
        wirelessConnections.clear();

        // re-register all connected receivers that do NOT have a connection
        for (PortModel port : connectedReceivers) {
            if (port.isConnected()) {
                continue;
            }
            ConnectionModel autoConnection = workspaceModel.getAutoConnectIndex().registerReceiver(port);
            if (autoConnection != null && App.LOG_POTENTIAL_BUGS) {
                /**
                 * There cannot be an auto-connection, since the pasted block
                 * caused the auto-connection in the first place, so there are
                 * no other candidate transmitters next in line. Undo can only
                 * be done directly after pasting, so there cannot be an
                 * intermediate step where another transmitter is created and
                 * generates an auto-connection.
                 */
                System.out.println("GENERATING AUTO CONNECTION FOR CONNECTED RECEIVER OF PASTED BLOCK");
            }
        }

        // remove pasted blocks and connections
        for (BlockModel block : pastedBlocks) {
            workspaceModel.removeBlockModel(block);
        }

        for (ConnectionModel connection : pastedConnections) {
            workspaceModel.removeConnectionModel(connection);
        }
    }
}

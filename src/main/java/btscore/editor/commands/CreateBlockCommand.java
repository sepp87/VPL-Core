package btscore.editor.commands;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import btscore.graph.block.BlockFactory;
import btscore.graph.block.BlockModel;
import btscore.workspace.WorkspaceModel;
import btscore.editor.context.UndoableCommand;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortModel;

/**
 *
 * @author Joost
 */
public class CreateBlockCommand implements UndoableCommand {

    private final String blockIdentifier;
    private final Point2D location;
    private final WorkspaceModel workspaceModel;
    private BlockModel blockModel;
    private final List<ConnectionModel> wirelessConnections = new ArrayList<>();

    public CreateBlockCommand(WorkspaceModel workspaceModel, String blockIdentifier, Point2D location) {
        this.workspaceModel = workspaceModel;
        this.blockIdentifier = blockIdentifier;
        this.location = location;
    }

    @Override
    public boolean execute() {
        if (blockModel == null) {
            blockModel = BlockFactory.createBlock(blockIdentifier);
            blockModel.layoutXProperty().set(location.getX());
            blockModel.layoutYProperty().set(location.getY());
            
        } else { // redo triggered
            blockModel.revive();
        }
        
        workspaceModel.addBlockModel(blockModel);

        // auto-connect transmitters
        wirelessConnections.clear();
        List<PortModel> transmitters = blockModel.getTransmittingPorts();
        for (PortModel port : transmitters) {
            List<ConnectionModel> autoConnections = workspaceModel.getWirelessIndex().registerTransmitter(port);
            wirelessConnections.addAll(autoConnections);
        }
        
        // auto-connect receivers
        List<PortModel> receivers = blockModel.getReceivingPorts();
        for (PortModel port : receivers) {
            ConnectionModel autoConnection = workspaceModel.getWirelessIndex().registerReceiver(port);
            if (autoConnection != null) {
                wirelessConnections.add(autoConnection);
            }
        }
        
        // place wireless connections on the workspace
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.addConnectionModel(connection);
        }

        System.out.println("PENDING RECEIVERS " + workspaceModel.getWirelessIndex().pendingReceivers.size());
        
        return true;
    }

    /**
     * Info - there is no need to record the index of the transmitter when
     * removing it from the registry, since this newly added block is always
     * last in the list
     */
    @Override
    public void undo() {

        // first unregister all transmitters, so receivers won't auto-connect to them again 
        List<PortModel> transmitters = blockModel.getTransmittingPorts();
        for (PortModel port : transmitters) {
            workspaceModel.getWirelessIndex().unregisterTransmitter(port);
        }

        // now connected receivers can be safely registered to await a new transmitter, without causing connections with the freshly removed transmitters
        for (PortModel transmitter : transmitters) {
            for (PortModel port : transmitter.getConnectedPorts()) {
                workspaceModel.getWirelessIndex().registerReceiver(port);
            }
        }

        // remove all receivers of the block itself
        for (PortModel port : blockModel.getReceivingPorts()) {
            workspaceModel.getWirelessIndex().unregisterReceiver(port);

        }

        workspaceModel.removeBlockModel(blockModel);
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.removeConnectionModel(connection);
        }

    }

}



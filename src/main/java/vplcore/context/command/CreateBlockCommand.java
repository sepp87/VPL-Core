package vplcore.context.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;
import vplcore.graph.block.BlockFactory;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortModel;

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

            List<PortModel> transmitters = blockModel.getTransmittingPorts();
            for (PortModel port : transmitters) {
                List<ConnectionModel> autoConnections = workspaceModel.getWirelessIndex().registerTransmitter(port);
                wirelessConnections.addAll(autoConnections);
            }

            List<PortModel> receivers = blockModel.getReceivingPorts();
            for (PortModel port : receivers) {
                ConnectionModel autoConnection = workspaceModel.getWirelessIndex().registerReceiver(port);
                if (autoConnection != null) {
                    wirelessConnections.add(autoConnection);
                }
            }

        } else { // redo triggered
            blockModel.revive();
            for (ConnectionModel connection : wirelessConnections) {
                connection.revive();
            }
        }
        workspaceModel.addBlockModel(blockModel);
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.addConnectionModel(connection);
        }

        return true;
    }

    private final Map<Integer, List<PortModel>> recordedTransmitters = new HashMap<>();
    private final List<PortModel> recordedReceivers = new ArrayList<>();

    @Override
    public void undo() {
        List<PortModel> transmitters = blockModel.getTransmittingPorts();
        for (PortModel port : transmitters) {
            int index = workspaceModel.getWirelessIndex().getTransmitterIndex(port);
            recordedTransmitters.computeIfAbsent(index, k -> new ArrayList<>()).add(port);
            for (ConnectionModel connection : port.getConnections()) {
                recordedReceivers.add(connection.getEndPort());
            }
        }

        for (PortModel port : transmitters) {
            workspaceModel.getWirelessIndex().unregisterTransmitter(port);
        }

        for (PortModel port : recordedReceivers) {
            workspaceModel.getWirelessIndex().registerReceiver(port);
        }

        workspaceModel.removeBlockModel(blockModel);
        for (ConnectionModel connection : wirelessConnections) {
            workspaceModel.removeConnectionModel(connection);
        }

    }

}

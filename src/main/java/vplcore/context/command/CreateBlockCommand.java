package vplcore.context.command;

import java.util.ArrayList;
import java.util.List;
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
    private final List<ConnectionModel> connections = new ArrayList<>();

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
                connections.addAll(autoConnections);
            }

            List<PortModel> receivers = blockModel.getReceivingPorts();
            for (PortModel port : receivers) {
                ConnectionModel autoConnection = workspaceModel.getWirelessIndex().registerReceiver(port);
                if (autoConnection != null) {
                    connections.add(autoConnection);
                }
            }

        } else {
            blockModel.revive();
        }
        workspaceModel.addBlockModel(blockModel);
        return true;
    }

    @Override
    public void undo() {
        workspaceModel.removeBlockModel(blockModel);
    }

}

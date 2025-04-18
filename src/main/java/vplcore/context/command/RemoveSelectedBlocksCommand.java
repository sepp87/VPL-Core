package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import vplcore.App;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.group.BlockGroupModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;
import vplcore.graph.port.PortModel;

/**
 *
 * @author Joost
 */
public class RemoveSelectedBlocksCommand implements UndoableCommand {

    private final WorkspaceController workspaceController;
    private final Collection<BlockController> blocks;
    private final Collection<ConnectionModel> connections = new HashSet<>();
    private final Collection<ConnectionModel> autoConnections = new HashSet<>();
    private final Map<BlockGroupModel, List<BlockModel>> blockGroups = new TreeMap<>();
    private final Map<Integer, List<PortModel>> recordedTransmitters = new TreeMap<>(); // use treemap to ensure looping is done by index ascending

    public RemoveSelectedBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
        this.blocks = workspaceController.getSelectedBlockControllers();
    }

    @Override
    public boolean execute() {
        if (App.LOG_METHOD_CALLS) {
            System.out.println("RemoveSelectedBlocksCommand.execute()");
        }
        workspaceController.deselectAllBlocks();

        // first retrieve all groups, because they are removed if the number of grouped blocks is reduced below two
        WorkspaceModel workspaceModel = workspaceController.getModel();
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            BlockGroupModel group = workspaceModel.getBlockGroup(blockModel);
            if (group == null) {
                continue;
            }
            if (blockGroups.containsKey(group)) {
                List<BlockModel> blocksInGroup = blockGroups.get(group);
                blocksInGroup.add(blockModel);
            } else {
                List<BlockModel> blocksInGroup = new ArrayList<>();
                blocksInGroup.add(blockModel);
                blockGroups.put(group, blocksInGroup);
            }
        }

        // also first retrieve connected receivers to connect to the next transmitter in line after removal, because they are gone as soon as the connections are removed
        recordedTransmitters.clear();
//        List<PortModel> connectedReceivers = new ArrayList<>();
        Set<PortModel> connectedReceivers = new HashSet<>();
        List<PortModel> unregisterTransmitters = new ArrayList<>();
//        System.out.println("Blocks found " + blocks.size());
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            List<PortModel> transmitters = blockModel.getTransmittingPorts();
//            System.out.println("Transmitters found " + transmitters.size());
            for (PortModel transmitter : transmitters) {
                int index = workspaceModel.getWirelessIndex().getTransmitterIndex(transmitter);
//                System.out.println("record " + index);
                recordedTransmitters.computeIfAbsent(index, k -> new ArrayList<>()).add(transmitter);
            }
            unregisterTransmitters.addAll(transmitters);

            for (PortModel transmitter : transmitters) {
                connectedReceivers.addAll(transmitter.getConnectedPorts());
            }
        }
        for (PortModel transmitter : unregisterTransmitters) {
            workspaceModel.getWirelessIndex().unregisterTransmitter(transmitter);
        }
//        System.out.println("Connected receivers found " + connectedReceivers.size());

        // execute removal
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            // remove connections before, otherwise the connections are already removed from the block
            Collection<ConnectionModel> removedConnections = workspaceModel.removeConnectionModels(blockModel);
            connections.addAll(removedConnections);
            workspaceModel.removeBlockModel(blockModel);
            workspaceModel.removeBlockFromGroup(blockModel); // can be null and could also be removed
        }

        // finally, if available, auto-connect receivers to next transmitters in line
        autoConnections.clear(); // clear set, because each auto-connection is a new connection instance
        for (PortModel port : connectedReceivers) {
            if (!port.getConnections().isEmpty()) {
                System.out.println("RECEIVER ALREADY CONNECTED in EXECUTE");
//                continue;
            }
            ConnectionModel autoConnection = workspaceModel.getWirelessIndex().registerReceiver(port);
//            System.out.println("Auto connection found " + autoConnection);
            if (autoConnection != null) {
                autoConnections.add(autoConnection);
                workspaceModel.addConnectionModel(autoConnection);
            }
        }
//        System.out.println("PENDING RECEIVERS in EXECUTE " + workspaceModel.getWirelessIndex().pendingReceivers.size());

        return true;

    }

    @Override
    public void undo() {
        workspaceController.deselectAllBlocks();

        // first revive all blocks, because connections could have been between blocks that have both been removed and groups could have been removed, since the number of grouped blocks was reduced below two
        WorkspaceModel workspaceModel = workspaceController.getModel();
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            blockModel.revive();
            workspaceModel.addBlockModel(blockModel);
            workspaceController.selectBlock(blockModel);
        }

        // remove all auto-connnections
        for (ConnectionModel connection : autoConnections) {
            workspaceModel.removeConnectionModel(connection);
        }

        // revive connections (also wireless)
        for (ConnectionModel connection : connections) {
            connection.revive();
            workspaceModel.addConnectionModel(connection);
        }

        // regroup blocks, if needed revive the group
        for (BlockGroupModel group : blockGroups.keySet()) {
            if (group.isRemoved()) {
                group.revive();
            }
            group.setBlocks(blockGroups.get(group));
            workspaceModel.addBlockGroupModel(group);
        }

        // register transmitters without generating connections, since all connections are revived
        for (Entry<Integer, List<PortModel>> portsByIndex : recordedTransmitters.entrySet()) {

            int index = portsByIndex.getKey();
//            System.out.println("re-register " + index);
            for (PortModel port : portsByIndex.getValue()) {
                // WARNING when silent is false, transmitter is connecting, because all connections (also wireless) were revived. Meaning there is a pending receiver, that is actually already connected again
                List<ConnectionModel> result = workspaceModel.getWirelessIndex().registerTransmitter(index, port, true);
                if (!result.isEmpty() && App.LOG_POTENTIAL_BUGS) {
                    System.out.println("GENERATING AUTOCONNECTIONS FOR TRANSMITTERS " + result.size());
                }
            }
        }
//        System.out.println("PENDING RECEIVERS in UNDO before registering receivers " + workspaceModel.getWirelessIndex().pendingReceivers.size());

        // register receivers without generating connections, since all connections are revived
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            List<PortModel> receivers = blockModel.getReceivingPorts();
            for (PortModel port : receivers) {
                if (!port.getConnections().isEmpty()) {
                    System.out.println("RECEIVER ALREADY CONNECTED");
//                    continue;
                }
                ConnectionModel result = workspaceModel.getWirelessIndex().registerReceiver(port); // TODO double check here
                if (result != null && App.LOG_POTENTIAL_BUGS) {
                    System.out.println("GENERATING AUTOCONNECTIONS FOR RECEIVERS");
                }
            }
        }
//        System.out.println("PENDING RECEIVERS in UNDO " + workspaceModel.getWirelessIndex().pendingReceivers.size());

//        System.out.println("CONNECTIONS ON WORKSPACE " + workspaceModel.getConnectionModels().size());
    }
}

//FOR REMOVEBLOCKSCOMMAND
//REDO
//            for (Entry<Integer, List<PortModel>> portsByIndex : recordedTransmitters.entrySet()) {
//                int index = portsByIndex.getKey();
//                for (PortModel port : portsByIndex.getValue()) {
//                    List<ConnectionModel> autoConnections = workspaceModel.getWirelessIndex().registerTransmitter(index, port);
//                    wirelessConnections.addAll(autoConnections);
//                }
//            }
//
//UNDO
//        recordedTransmitters.clear();
//        List<PortModel> connectedReceivers = new ArrayList<>();
//        List<PortModel> transmitters = blockModel.getTransmittingPorts();
//        for (PortModel port : transmitters) {
//            int index = workspaceModel.getWirelessIndex().getTransmitterIndex(port);
//            recordedTransmitters.computeIfAbsent(index, k -> new ArrayList<>()).add(port);
//            for (ConnectionModel connection : port.getConnections()) {
//                connectedReceivers.add(connection.getEndPort());
//            }
//        }

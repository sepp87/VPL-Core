package btscore.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import btscore.App;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortModel;

/**
 * Registers ports and connections flagged as wireless. wireless transmitting
 * ports get registered by datatype. when a new block is created with a wireless
 * receiving port, it checks if there is wireless transmitting port registered
 * with that datatype. if there is one or more transmitters available, the
 * receiver will link to the first in the list. if there is none, the receiver
 * will register itself to await for a transmitter. Should a transmitter be
 * removed, all its connected receivers will remain pending for a new suitable
 * transmitter.
 *
 * @author joostmeulenkamp
 */
public class AutoConnectIndex {

    private final Map<Class<?>, List<PortModel>> transmitters = new HashMap<>();
    public final Map<Class<?>, List<PortModel>> pendingReceivers = new HashMap<>();

    public void registerEligiblePorts(BlockModel block) {
        for (PortModel transmitter : block.getTransmittingPorts()) {
            registerTransmitter(transmitter);
        }
        for (PortModel receiver : block.getReceivingPorts()) {
            registerReceiver(receiver);
        }
    }

    public void unregisterEligiblePorts(BlockModel block) {
        for (PortModel transmitter : block.getTransmittingPorts()) {
            unregisterTransmitter(transmitter);
        }
        for (PortModel receiver : block.getReceivingPorts()) {
            unregisterReceiver(receiver);
        }
    }

    public List<ConnectionModel> registerTransmitter(PortModel transmitter) {
        return registerTransmitter(null, transmitter, false);
    }

    public List<ConnectionModel> registerTransmitter(Integer index, PortModel transmitter, boolean silent) {
        Class<?> type = transmitter.getDataType();
        if (index == null) {
            transmitters.computeIfAbsent(type, key -> new ArrayList<>()).add(transmitter);
        } else {
            transmitters.get(type).add(index, transmitter);
        }

        // Connect any waiting receivers
        List<ConnectionModel> result = new ArrayList<>();
        if (pendingReceivers.containsKey(type)) {
            // TODO connect
            for (PortModel receiver : pendingReceivers.get(type)) {
                if (!receiver.getConnections().isEmpty() && App.LOG_POTENTIAL_BUGS) {
                    System.out.println("WARNING: Receiver was already connected eventhough it is registered. This issue might occur when reviving wireless connections");
                }
                if (silent) {
                    continue;
                }
                if (ConnectionModel.isEligible(transmitter, receiver)) {
                    ConnectionModel connection = new ConnectionModel(transmitter, receiver);
                    result.add(connection);
                }
            }
            pendingReceivers.remove(type); // assuming one match needed
        }
        return result;
    }

    public ConnectionModel registerReceiver(PortModel receiver) {
        if (!receiver.getConnections().isEmpty() && App.LOG_POTENTIAL_BUGS) {
            System.out.println("WARNING: Receiver was already connected before registering. Evaluate if this case should be caught");
        }
        Class<?> type = receiver.getDataType();
        List<PortModel> existing = transmitters.get(type);
        if (existing != null && !existing.isEmpty()) {
            PortModel transmitter = existing.get(0);
            if (ConnectionModel.isEligible(transmitter, receiver)) {
                return new ConnectionModel(transmitter, receiver);
            }
        } else {
            pendingReceivers.computeIfAbsent(type, key -> new ArrayList<>()).add(receiver);
        }
        return null;
    }

    public int getTransmitterIndex(PortModel transmitter) {
        int result = -1;
        Class<?> type = transmitter.getDataType();
        if (transmitters.containsKey(type)) {
            return transmitters.get(type).indexOf(transmitter);
        }
        if (result == -1 && App.LOG_POTENTIAL_BUGS) {
            System.out.println("ERROR: Transmitter was NOT found, eventhough it should have been registered.");
        }
        return result;
    }

    public void unregisterTransmitter(PortModel transmitter) {
        Class<?> type = transmitter.getDataType();
        if (transmitters.containsKey(type)) {
            transmitters.get(type).remove(transmitter);
        }
        // get all connected receivers
        // connect all receivers to next in line
        // or put all receivers into pending
    }

    public void unregisterReceiver(PortModel receiver) {
        Class<?> type = receiver.getDataType();
        if (pendingReceivers.containsKey(type)) {
            pendingReceivers.get(type).remove(receiver);
        }
        // if receivers are not indexed - nothing todo anymore

    }

}

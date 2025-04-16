package vplcore.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortModel;

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
public class WirelessIndex {

    private static final Map<Class<?>, List<PortModel>> transmitters = new HashMap<>();
    private static final Map<Class<?>, List<PortModel>> pendingReceivers = new HashMap<>();

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
        Class<?> type = transmitter.getDataType();
        transmitters.computeIfAbsent(type, key -> new ArrayList<>()).add(transmitter);

        // Connect any waiting receivers
        List<ConnectionModel> result = new ArrayList<>();
        if (pendingReceivers.containsKey(type)) {
            // TODO connect
            for (PortModel receiver : pendingReceivers.get(type)) {
                ConnectionModel connection = new ConnectionModel(transmitter, receiver);
                result.add(connection);
            }
            pendingReceivers.remove(type); // assuming one match needed
        }
        return result;
    }

    public ConnectionModel registerReceiver(PortModel receiver) {
        Class<?> type = receiver.getDataType();
        List<PortModel> existing = transmitters.get(type);
        if (existing != null && !existing.isEmpty()) {
            PortModel transmitter = existing.get(0);
            return new ConnectionModel(transmitter, receiver);
        } else {
            pendingReceivers.computeIfAbsent(type, key -> new ArrayList<>()).add(receiver);
        }
        return null;
    }

    private void unregisterTransmitter(PortModel transmitter) {
        Class<?> type = transmitter.getDataType();
        if (transmitters.containsKey(type)) {
            transmitters.get(type).remove(transmitter);
        }
        // get all connected receivers
        // connect all receivers to next in line
        // or put all receivers into pending
    }

    private void unregisterReceiver(PortModel receiver) {
        Class<?> type = receiver.getDataType();
        if (pendingReceivers.containsKey(type)) {
            pendingReceivers.get(type).remove(receiver);
        }
        // if receivers are not indexed - nothing todo anymore

    }

}

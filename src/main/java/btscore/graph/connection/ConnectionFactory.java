package btscore.graph.connection;

import btscore.graph.port.PortModel;

/**
 *
 * @author joostmeulenkamp
 */
public class ConnectionFactory {

    public static ConnectionModel createConnection(PortModel startPort, PortModel endPort) {
        return new ConnectionModel(startPort, endPort);
    }
}

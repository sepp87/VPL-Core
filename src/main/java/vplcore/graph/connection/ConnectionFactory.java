package vplcore.graph.connection;

import vplcore.graph.port.PortModel;

/**
 *
 * @author joostmeulenkamp
 */
public class ConnectionFactory {

    public static ConnectionModel createConnection(PortModel startPort, PortModel endPort) {
        return new ConnectionModel(startPort, endPort);
    }
}

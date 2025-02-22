package vplcore.graph.connection;

import vplcore.graph.port.PortModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jo.vpl.xml.ConnectionTag;
import vplcore.graph.base.BaseModel;

///**
// * Extension idea - see PortModel
// *
// * @author Joost
// */
//public class ConnectionModel {
//
//    private final PortModel sending;
//    private final PortModel receiving;
//
//    public ConnectionModel(PortModel sending, PortModel receiving) throws IllegalArgumentException {
//        this.sending = sending;
//        this.receiving = receiving;
//        bindData();
//
//    }
//
//    private void onSendingDataChanged(Object b, Object o, Object newData) {
//        Object data = copyData(newData);
//    }
//
//    private Object copyData(Object data) {
//        return data;
//    }
//
//    private void bindData() {
//        receiving.dataProperty().bind(sending.dataProperty());
//    }
//
//    public void remove() {
//        sending.removeConnection(this);
//        receiving.removeConnection(this);
//        unbindData();
//    }
//
//    private void unbindData() {
//        receiving.dataProperty().unbind();
//    }
//}
/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionModel extends BaseModel {

    protected final StringProperty id = new SimpleStringProperty();

    private final PortModel startPort;
    private final PortModel endPort;

    public ConnectionModel(PortModel startPort, PortModel endPort) {
        this.startPort = startPort;
        this.endPort = endPort;

        this.startPort.setActive(true);
        this.endPort.setActive(true);

        startPort.connectedConnections.add(this);
        endPort.connectedConnections.add(this);

        endPort.calculateData(startPort.getData());

        startPort.dataProperty().addListener(endPort.getStartPortDataChangedListener());
    }

    public PortModel getStartPort() {
        return startPort;
    }

    public PortModel getEndPort() {
        return endPort;
    }

    public void remove() {
        startPort.connectedConnections.remove(this);
        endPort.connectedConnections.remove(this);

        if (startPort.connectedConnections.isEmpty()) {
            startPort.setActive(false);
        }

        if (endPort.connectedConnections.isEmpty()) {
            endPort.setActive(false);
        }

        startPort.dataProperty().removeListener(endPort.getStartPortDataChangedListener());

        endPort.calculateData();
    }

    public void serialize(ConnectionTag xmlTag) {
        xmlTag.setStartBlock(startPort.parentBlock.idProperty().get());
        xmlTag.setStartIndex(startPort.index);
        xmlTag.setEndBlock(endPort.parentBlock.idProperty().get());
        xmlTag.setEndIndex(endPort.index);
    }
}

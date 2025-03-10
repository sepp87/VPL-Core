package vplcore.graph.connection;

import vplcore.graph.port.PortModel;
import jo.vpl.xml.ConnectionTag;
import vplcore.graph.base.BaseModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionModel extends BaseModel {

    private final PortModel startPort;
    private final PortModel endPort;

    public ConnectionModel(PortModel startPort, PortModel endPort) {
        this.startPort = startPort;
        this.endPort = endPort;
        initialize();
    }

    private void initialize() {
        startPort.addConnection(this);
        endPort.addConnection(this);
        endPort.calculateData(startPort.getData());
        startPort.dataProperty().addListener(endPort.getStartPortDataChangedListener());
    }

    public PortModel getStartPort() {
        return startPort;
    }

    public PortModel getEndPort() {
        return endPort;
    }

    @Override
    public void remove() {
        startPort.removeConnection(this);
        endPort.removeConnection(this);
        startPort.dataProperty().removeListener(endPort.getStartPortDataChangedListener());
        endPort.calculateData();
        super.remove();
    }

    @Override
    public void revive() {
        initialize();
        super.revive();
    }

    public void serialize(ConnectionTag xmlTag) {
        xmlTag.setStartBlock(startPort.getBlock().idProperty().get());
        xmlTag.setStartIndex(startPort.getIndex());
        xmlTag.setEndBlock(endPort.getBlock().idProperty().get());
        xmlTag.setEndIndex(endPort.getIndex());
    }
}

///**
// * Extension idea - see PortModel
// *
// * @author JoostMeulenkamp
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

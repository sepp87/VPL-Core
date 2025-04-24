package btscore.graph.connection;

import btscore.graph.port.PortModel;
import btsxml.ConnectionTag;
import btscore.App;
import btscore.Config;
import btscore.graph.base.BaseModel;
import btscore.graph.port.PortType;
import btscore.utils.TypeCastUtils;

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

    public boolean isWireless() {
        return startPort.autoConnectableProperty().get();
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
        startPort.dataProperty().removeListener(endPort.getStartPortDataChangedListener());
        startPort.removeConnection(this);
        endPort.removeConnection(this);
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

    public static boolean isEligible(PortModel startPortModel, PortModel endPortModel) {
        boolean differentPortTypes = endPortModel.getPortType() != startPortModel.getPortType();
        boolean differentBlocks = !endPortModel.getBlock().equals(startPortModel.getBlock());

        return isTypeCompatible(startPortModel, endPortModel) && differentPortTypes && differentBlocks;
    }

    private static boolean isTypeCompatible(PortModel startPortModel, PortModel endPortModel) {
        if (!App.TYPE_SENSITIVE) {
            return true;
        }

        boolean isInput = endPortModel.getPortType() == PortType.INPUT;
        Class<?> outputType = isInput ? startPortModel.getDataType() : endPortModel.getDataType();
        Class<?> inputType = isInput ? endPortModel.getDataType() : startPortModel.getDataType();

        return TypeCastUtils.isCastableTo(outputType, inputType);
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

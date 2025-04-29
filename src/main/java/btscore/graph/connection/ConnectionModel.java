package btscore.graph.connection;

import btscore.graph.port.PortModel;
import btsxml.ConnectionTag;
import btscore.App;
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

    @Override
    public void setActive(boolean active) {
        if (active == isActive()) {
            return; // no need to forward data again
        }
        super.setActive(active);

        if (isActive()) {
            forwardData(); // initial data flow

            // Force processing when initially activated with null data
            // This allows blocks to handle null input appropriately e.g. throw an exception or fall back to defaults
            if (startPort.getData() == null) {
                endPort.getBlock().processSafely();
            }
        } else {
            // TODO
        }

    }

    public void forwardData() {
        if (!isActive()) {
            return;
        }
        endPort.setData(startPort.getData());
    }

    public boolean isAutoConnectable() {
        return startPort.autoConnectableProperty().get();
    }

    private void initialize() {
        // add connection to ports here, to ensure connections are re-added on revival
        startPort.addConnection(this);
        endPort.addConnection(this);
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

        // Force processing when removing a connection with null data
        // This maintains consistency with setActive() behavior
        if (startPort.getData() == null) {
            endPort.getBlock().processSafely();
        }
        super.setActive(false);
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
        boolean typesCompatible = isTypeCompatible(startPortModel, endPortModel);

        return typesCompatible && differentPortTypes && differentBlocks;
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

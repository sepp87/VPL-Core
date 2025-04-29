package btscore.graph.port;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import btscore.graph.base.BaseModel;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.utils.ObjectUtils;
import btscore.utils.TypeCastUtils;

/**
 *
 * @author JoostMeulenkamp
 */
public class PortModel extends BaseModel {

    private final BooleanProperty autoConnectable = new SimpleBooleanProperty(this, "autoConnectable", false);
    private final ObjectProperty<Object> data = new SimpleObjectProperty<>(this, "data", null);
    private final ObservableSet<ConnectionModel> connections = FXCollections.observableSet();
    private final ObjectProperty<Class<?>> dataType = new SimpleObjectProperty<>(this, "dataType", null);

    private final PortType portType;
    private final int index;
    private final boolean multiDockAllowed;
    private final BlockModel block;

    public PortModel(String name, PortType portType, Class<?> type, BlockModel block, boolean multiDockAllowed) {
        this.nameProperty().set(name);
        this.portType = portType;
        this.index = (portType == PortType.INPUT) ? block.getInputPorts().size() : block.getOutputPorts().size();
        this.block = block;
        this.multiDockAllowed = multiDockAllowed;
        this.dataType.set(type);
    }

    @Override
    public void setActive(boolean isActive) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("PortModel controls its own active state.");
    }

    public BooleanProperty autoConnectableProperty() {
        return autoConnectable;
    }

    public PortType getPortType() {
        return portType;
    }

    public int getIndex() {
        return index;
    }

    public boolean isMultiDockAllowed() {
        return multiDockAllowed;
    }

    public BlockModel getBlock() {
        return block;
    }

    public ObjectProperty<Class<?>> dataTypeProperty() {
        return dataType;
    }

    public Class<?> getDataType() {
        return dataType.get();
    }

    public ObjectProperty<Object> dataProperty() {
        return data;
    }

    public void setData(Object newData) {
        Object oldData = data.get();
        if (ObjectUtils.compare(oldData, newData)) {
            return;
        }
        data.set(newData);

        if (portType == PortType.OUTPUT) {
            publishData();

        } else { // PortType.INPUT
            preprocessData(newData);
        }
    }

    private void publishData() {
        for (ConnectionModel connection : connections) {
            connection.forwardData();
        }
    }

    public void preprocessData(Object value) {

        if (!connections.isEmpty()) { // incoming data of one single incoming connection
            System.out.println(block.getClass().getSimpleName() + " received: " + value);

            //Cast all primitive dataType to String if this port dataType is String
            PortModel startPort = connections.iterator().next().getStartPort();
            if (this.getDataType() == String.class && TypeCastUtils.contains(startPort.getDataType())) {

                if (startPort.getData() instanceof List) {
                    List<?> list = (List) startPort.getData();
                    List<String> newList = new ArrayList<>();
                    for (Object primitive : list) {
                        newList.add(primitive + "");
                    }
                    data.set(newList);
                } else {
                    data.set(startPort.getData() + "");
                }
            } else { // this INPUT port does NOT have data type String
                data.set(startPort.getData());
            }
        }
    }

    public Object getData() {
        return data.get();
    }

    public void addConnection(ConnectionModel connection) {
        connections.add(connection);
        active.set(true);

        if (portType == PortType.INPUT) {
            Object sourceData = connection.getStartPort().getData();
            block.onIncomingConnectionAdded(sourceData);
        }
    }

    public void removeConnection(ConnectionModel connection) {

        connections.remove(connection);
        active.set(!connections.isEmpty());

        if (!isActive() && portType == PortType.INPUT) {
            this.data.set(null);
        }

        if (portType == PortType.INPUT) {
            Object sourceData = connection.getStartPort().getData();
            block.onIncomingConnectionRemoved(sourceData);
        }
    }

    public ObservableSet<ConnectionModel> getConnections() {
        return FXCollections.unmodifiableObservableSet(connections);
    }

    public boolean isConnected() {
        return !connections.isEmpty();
    }

    public List<PortModel> getConnectedPorts() {
        List<PortModel> result = new ArrayList<>();
        for (ConnectionModel connection : connections) {
            if (this.portType == PortType.OUTPUT) {
                result.add(connection.getEndPort());
            } else {
                result.add(connection.getStartPort());
            }
        }
        return result;
    }

    @Override
    public void remove() {
        connections.clear();
        super.remove();
    }

    @Override
    public void revive() {
        super.revive();
    }
}

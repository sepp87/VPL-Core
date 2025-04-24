package btscore.graph.port;

//import javafx.beans.property.DoubleProperty;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import btscore.graph.base.BaseModel;
import btscore.graph.block.BlockModel;
import btscore.graph.connection.ConnectionModel;
import btscore.utils.TypeCastUtils;

//import javafx.beans.property.ObjectProperty;
//import javafx.beans.property.SimpleDoubleProperty;
//import javafx.beans.property.SimpleObjectProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableSet;
//
///**
// * Extension idea - allow multiple incoming connections e.g. to join strings,
// * lists or whatever (multipleIncomingAllowed)
// *
// * @author Joost
// */
//public class PortModel {
//
//    // 
//    private final StringProperty name = new SimpleStringProperty();
//    private final ObjectProperty<Object> data = new SimpleObjectProperty(null);
//    private final ObjectProperty<Class> dataType = new SimpleObjectProperty(); // e.g. String, Number, int, double, float
//    private final ObjectProperty<Class> dataStructure = new SimpleObjectProperty(); // e.g. List, Array, Map, Set
//
//    private final ObservableSet<ConnectionModel> outgoing;
//    private final ObjectProperty<ConnectionModel> incoming;
//
//    public PortModel(String name, Class dataType, Class dataStructure, boolean isInput) {
//        this.name.set(name);
//        this.dataType.set(dataType);
//        this.dataStructure.set(dataStructure);
//
//        this.outgoing = isInput ? null : FXCollections.observableSet();
//        this.incoming = isInput ? new SimpleObjectProperty() : null;
//    }
//
//    public boolean isInput() {
//        return outgoing == null;
//    }
//
//    public boolean isOutput() {
//        return outgoing != null;
//    }
//
//    public StringProperty nameProperty() {
//        return name;
//    }
//
//    public ObjectProperty<Object> dataProperty() {
//        return data;
//    }
//
//    public ObjectProperty<Class> dataTypeProperty() {
//        return dataType;
//    }
//
//    public void addConnection(ConnectionModel connectionModel) {
//        if (isInput()) {
//            incoming.set(connectionModel);
//        } else {
//            outgoing.add(connectionModel);
//        }
//    }
//
//    public void removeConnection(ConnectionModel connectionModel) {
//        if (isInput()) {
//            incoming.set(null);
//            data.set(null); // TODO might want to revert to default data here
//        } else {
//            outgoing.remove(connectionModel);
//        }
//    }
//
//    public void remove() {
//        if (isInput()) {
//            incoming.get().remove();
//        } else {
//            for (ConnectionModel connection : outgoing) {
//                connection.remove();
//            }
//        }
//    }
//}
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

        this.connections.addListener(connectionsListener);
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

    public void setData(Object value) {
        calculateData(value);
    }

    public Object getData() {
        return data.get();
    }

    public void addConnection(ConnectionModel connection) {
        connections.add(connection);

    }

    public void removeConnection(ConnectionModel connection) {
        connections.remove(connection);
    }

    public ObservableSet<ConnectionModel> getConnections() {
//        return new ArrayList<>(connections);
        return connections;
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

    private final SetChangeListener<ConnectionModel> connectionsListener = this::onConnectionsChanged;

    private void onConnectionsChanged(Change<? extends ConnectionModel> change) {
        System.out.println("PortModel.onConnectionsChanged() connections.size() " + connections.size());
        boolean isActive = !connections.isEmpty();
        active.set(isActive);
        if (!isActive && portType == PortType.INPUT) {
            data.set(null);
        }

        if (portType == PortType.INPUT) {
            if (change.wasAdded()) {
                Object incomingData = change.getElementAdded().getStartPort().getData();
                block.onIncomingConnectionAdded(incomingData);
            } else {
                Object removedData = change.getElementRemoved().getStartPort().getData();
                block.onIncomingConnectionRemoved(removedData);
            }
        }

    }

    public ChangeListener<Object> getStartPortDataChangedListener() {
        return startPortDataChangedListener;
    }
    private final ChangeListener<Object> startPortDataChangedListener = this::handleStartPortDataChanged;

    private void handleStartPortDataChanged(ObservableValue obj, Object oldVal, Object newVal) {
        calculateData(newVal);
    }

    public void calculateData() {
        calculateData(null);
    }

    public void calculateData(Object value) {

        if (portType == PortType.INPUT) {

            if (multiDockAllowed && connections.size() > 1) {

                this.getDataType().cast(new Object());
                List listOfLists = new ArrayList<>();

                for (ConnectionModel connection : connections) {

                    //Cast all primitive dataType to String if this port dataType is String
                    PortModel startPort = connection.getStartPort();
                    if (this.getDataType() == String.class && TypeCastUtils.contains(startPort.getDataType())) {
                        if (startPort.getData() instanceof List) {
                            List list = (List) startPort.getData();
                            List newList = new ArrayList<>();
                            for (Object primitive : list) {
                                newList.add(primitive + "");
                            }
                            listOfLists.add(newList);
                        } else {
                            listOfLists.add(startPort.getData() + "");
                        }
                    } else {
                        listOfLists.add(startPort.getData());
                    }

                }
                data.set(listOfLists);

            } else if (!connections.isEmpty()) { // incoming data of one single incoming connection
//                System.out.println("Data Received: " + value);
                System.out.println("Data Received:");

                //Cast all primitive dataType to String if this port dataType is String
                PortModel startPort = connections.iterator().next().getStartPort();
                if (this.getDataType() == String.class && TypeCastUtils.contains(startPort.getDataType())) {

                    if (startPort.getData() instanceof List) {
                        List list = (List) startPort.getData();
                        List newList = new ArrayList<>();
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

            } else { // if there are no incoming connections, set data to null - REDUNDANT since data already set to null in onConnectionsChanged
                data.set(null);
            }
        } else { // if output port then simply set the data
            data.set(value);
        }
        //OnDataChanged();
    }

    @Override
    public void remove() {
        connections.removeListener(connectionsListener);
        connections.clear();
        super.remove();
    }

    @Override
    public void revive() {
        connections.addListener(connectionsListener);
        super.revive();
    }
}

//    private void calculateData(Object value) {
//        if (portType == Type.IN) {
//            if (multiDockAllowed && connectedConnections.size() > 1) {
//                List<Object> listOfData = connectedConnections.stream()
//                        .map(conn -> conn.getStartPort().getData())
//                        .collect(Collectors.toList());
//                data.set(listOfData);
//            } else if (!connectedConnections.isEmpty()) {
//                data.set(connectedConnections.get(0).getStartPort().getData());
//            } else {
//                data.set(null);
//            }
//        } else {
//            data.set(value);
//        }
//    }
//}

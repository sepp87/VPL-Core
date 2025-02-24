package vplcore.graph.port;

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
import vplcore.graph.base.BaseModel;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.util.TypeExtensions;

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

    private final StringProperty name = new SimpleStringProperty(this, "name", null);
    private final ObjectProperty<Object> data = new SimpleObjectProperty<>(this, "data", null);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
    public ObservableSet<ConnectionModel> connections = FXCollections.observableSet();
    public Class<?> dataType;
    public PortType portType;
    public int index;
    public boolean multiDockAllowed;

    public PortModel(String name, PortType portType, Class<?> type, BlockModel parentBlock, boolean multiDockAllowed) {
        this.name.set(name);
        this.portType = portType;
        this.dataType = type;
        this.index = (portType == PortType.IN) ? parentBlock.getInputPorts().size() : parentBlock.getOutputPorts().size();
        this.parentBlock = parentBlock;
        this.multiDockAllowed = multiDockAllowed;

        this.connections.addListener(connectionsListener);
    }

    public BlockModel parentBlock;

    public List<ConnectionModel> getConnections() {
        return new ArrayList<>(connections);
    }
    private final SetChangeListener<ConnectionModel> connectionsListener = this::onConnectionsChanged;

    private void onConnectionsChanged(Change<? extends ConnectionModel> change) {
        boolean isActive = !connections.isEmpty();
        active.set(isActive);
    }

    public final ObjectProperty<Object> dataProperty() {
        return data;
    }

    public final void setData(Object value) {
        calculateData(value);
    }

    public final Object getData() {
        return data.get();
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

        if (portType == PortType.IN) {

            if (multiDockAllowed && connections.size() > 1) {

                dataType.cast(new Object());
                List listOfLists = new ArrayList<>();

                for (ConnectionModel connection : connections) {

                    //Cast all primitive dataType to String if this port dataType is String
                    PortModel startPort = connection.getStartPort();
                    if (dataType == String.class && TypeExtensions.contains(startPort.dataType)) {
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

            } else if (connections.size() > 0) { // incoming data of one single incoming connection
                System.out.println("Data Received: " + value);

                //Cast all primitive dataType to String if this port dataType is String
                PortModel startPort = connections.iterator().next().getStartPort();
                if (dataType == String.class && TypeExtensions.contains(startPort.dataType)) {
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
                } else {
                    data.set(startPort.getData());
                }

            } else { // if there are no incoming connections, set data to null
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
        super.remove();
//        for (ConnectionModel connection : getConnections()) {
//            connection.remove();
//        }
//        connectedConnections.clear();
    }
}

//public class PortModel {
//    public enum Type {
//        IN, OUT
//    }
//
//    private final ObjectProperty<Object> data = new SimpleObjectProperty<>(null);
//    private final BooleanProperty active = new SimpleBooleanProperty(false);
//    private final StringProperty name = new SimpleStringProperty();
//    private final DoubleProperty centerX = new SimpleDoubleProperty();
//    private final DoubleProperty centerY = new SimpleDoubleProperty();
//    
//    private final ObservableList<ConnectionModel> connectedConnections = FXCollections.observableArrayList();
//    private final Type portType;
//    private final BlockModel parentBlock;
//    private final Class<?> dataType;
//    private final boolean multiDockAllowed;
//    private final int index;
//    
//    public PortModel(String name, BlockModel parent, Type portType, Class<?> type, boolean multiDockAllowed) {
//        this.name.set(name);
//        this.parentBlock = parent;
//        this.portType = portType;
//        this.dataType = type;
//        this.multiDockAllowed = multiDockAllowed;
//        this.index = (portType == Type.IN) ? parent.getInputPorts().size() : parent.getOutputPorts().size();
//        
//        this.connectedConnections.addListener((ListChangeListener<ConnectionModel>) change -> updateActiveState());
//    }
//    
//    public ObservableList<ConnectionModel> getConnectedConnections() {
//        return connectedConnections;
//    }
//    
//    public ObjectProperty<Object> dataProperty() {
//        return data;
//    }
//    
//    public BooleanProperty activeProperty() {
//        return active;
//    }
//    
//    public StringProperty nameProperty() {
//        return name;
//    }
//    
//    public DoubleProperty centerXProperty() {
//        return centerX;
//    }
//    
//    public DoubleProperty centerYProperty() {
//        return centerY;
//    }
//    
//    public void setData(Object value) {
//        calculateData(value);
//    }
//    
//    public Object getData() {
//        return data.get();
//    }
//    
//    private void updateActiveState() {
//        active.set(!connectedConnections.isEmpty());
//    }
//    
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

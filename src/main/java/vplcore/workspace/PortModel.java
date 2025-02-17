package vplcore.workspace;

//import javafx.beans.property.DoubleProperty;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
public class PortModel extends VBox {

    public enum Type {
        IN,
        OUT
    }

//    private final Point2D center = new Point2D(0, 0);
    private final ObjectProperty<Object> data = new SimpleObjectProperty<>(this, "data", null);
    private final BooleanProperty active = new SimpleBooleanProperty(this, "active", false);
    private final StringProperty name = new SimpleStringProperty(this, "name", null);

    public final DoubleProperty centerXProperty = new SimpleDoubleProperty();
    public final DoubleProperty centerYProperty = new SimpleDoubleProperty();

    public ObservableList<ConnectionModel> connectedConnections;
    public Class<?> dataType;
    public Type portType;
    public BlockModel parentBlock;
    public boolean multiDockAllowed;
    public int index;

    private final Tooltip tip;

    private final EventHandler<MouseEvent> portClickedForNewConnectionHandler = this::handlePortClickedForNewConnection;
    private final EventHandler<MouseEvent> portPressedHandler = this::handlePortPressed; // prevent block dragging
    private final EventHandler<MouseEvent> portDraggedHandler = this::handlePortDragged;
    private final ListChangeListener<ConnectionModel> portConnectionsChangedListener = this::handlePortConnectionsChanged;
    private final ChangeListener<Object> portActivationChangedListener = this::handlePortActivationChanged;
    private final ChangeListener<Object> portCoordinatesChangedListener = this::handlePortCoordinatesChanged;
    private final ChangeListener<Object> startPortDataChangedListener = this::handleStartPortDataChanged;

    public PortModel(String name, BlockModel parent, Type portType, Class<?> type) {
        tip = new Tooltip();
        Tooltip.install(this, tip);
        tip.textProperty().bind(this.nameProperty());

        this.parentBlock = parent;
        this.dataType = type;
        this.portType = portType;
        this.setName(name);

        if (portType == Type.IN) {
            index = parent.getInputPorts().size();
        } else {
            index = parent.getOutputPorts().size();
        }

        getStyleClass().add("port");
        getStyleClass().add("port-" + portType.toString().toLowerCase());

        connectedConnections = FXCollections.observableArrayList();

        connectedConnections.addListener(portConnectionsChangedListener);
        addEventHandler(MouseEvent.MOUSE_CLICKED, portClickedForNewConnectionHandler);
        addEventHandler(MouseEvent.MOUSE_PRESSED, portPressedHandler);
        addEventHandler(MouseEvent.MOUSE_DRAGGED, portDraggedHandler);
        active.addListener(portActivationChangedListener);
        parentBlock.layoutXProperty().addListener(portCoordinatesChangedListener);
        parentBlock.layoutYProperty().addListener(portCoordinatesChangedListener);
        boundsInParentProperty().addListener(portCoordinatesChangedListener);
    }

    public List<ConnectionModel> getConnectedConnections() {
        return new ArrayList<>(connectedConnections);
    }

    public void handlePortClickedForNewConnection(MouseEvent event) {
        if (event.isStillSincePress()) {
            parentBlock.workspaceController.initiateConnection(PortModel.this);

        }
        event.consume();
    }

    public void handlePortPressed(MouseEvent event) {
        event.consume();
    }

    private void handlePortConnectionsChanged(ListChangeListener.Change<? extends ConnectionModel> change) {
        if (connectedConnections.size() == 0) {
            setActive(false);
        } else {
            setActive(true);
        }
    }

    private void calcOrigin() {
        try {
            Point2D centerInScene = localToScene(getWidth() / 2, getHeight() / 2);
            Point2D centerInLocal = parentBlock.workspaceController.getView().sceneToLocal(centerInScene);

            centerXProperty.set(centerInLocal.getX());
            centerYProperty.set(centerInLocal.getY());
        } catch (Exception e) {
        }
    }

    private void handlePortCoordinatesChanged(ObservableValue<? extends Object> b, Object o, Object n) {
        calcOrigin();
    }

    /**
     * @TODO CHANGE FROM ORIGINAL CODE Consume event to prevent block from
     * moving around.
     *
     * @param e
     */
    private void handlePortDragged(MouseEvent e) {
        e.consume();
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

    private void handleStartPortDataChanged(ObservableValue obj, Object oldVal, Object newVal) {
        calculateData(newVal);
    }

    public void calculateData() {
        calculateData(null);
    }

    public void calculateData(Object value) {

        if (portType == Type.IN) {

            if (multiDockAllowed && connectedConnections.size() > 1) {

                dataType.cast(new Object());
                List listOfLists = new ArrayList<>();

                for (ConnectionModel connection : connectedConnections) {

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

            } else if (connectedConnections.size() > 0) { // incoming data of one single incoming connection
                System.out.println("Data Received: " + value);

                //Cast all primitive dataType to String if this port dataType is String
                PortModel startPort = connectedConnections.get(0).getStartPort();
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

    public final void setActive(boolean value) {
        active.set(value);
    }

    public final boolean isActive() {
        return active.get();
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public final void setName(String value) {
        name.set(value);
    }

    public final String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    private void handlePortActivationChanged(Object obj, Object oldVal, Object newVal) {
        if (isActive()) {
            getStyleClass().remove("port");
            getStyleClass().add("port-active");
        } else {
            getStyleClass().remove("port-active");
            getStyleClass().add("port");
        }
    }

    public void delete() {
        connectedConnections.removeListener(portConnectionsChangedListener);
        removeEventHandler(MouseEvent.MOUSE_CLICKED, portClickedForNewConnectionHandler);
        removeEventHandler(MouseEvent.MOUSE_PRESSED, portPressedHandler);
        removeEventHandler(MouseEvent.MOUSE_DRAGGED, portDraggedHandler);
        active.removeListener(portActivationChangedListener);
        parentBlock.layoutXProperty().removeListener(portCoordinatesChangedListener);
        parentBlock.layoutYProperty().removeListener(portCoordinatesChangedListener);
        boundsInParentProperty().removeListener(portCoordinatesChangedListener);

        tip.textProperty().unbind();

        for (ConnectionModel connection : getConnectedConnections()) {
            connection.remove();
        }

//        connectedConnections.clear();
    }
}

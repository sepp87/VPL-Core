package vplcore.graph.model;

import javafx.scene.layout.VBox;
import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.*;
import vplcore.graph.util.TypeExtensions;

/**
 *
 * @author JoostMeulenkamp
 */
public class Port extends VBox {

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

    public ObservableList<Connection> connectedConnections;
    public Class<?> dataType;
    public Type portType;
    public Block parentBlock;
    public boolean multiDockAllowed;
    public int index;

    private final Tooltip tip;

    private final EventHandler<MouseEvent> portClickedForNewConnectionHandler = this::handlePortClickedForNewConnection;
    private final EventHandler<MouseEvent> portPressedHandler = this::handlePortPressed; // prevent block dragging
    private final EventHandler<MouseEvent> portDraggedHandler = this::handlePortDragged;
    private final ListChangeListener<Connection> portConnectionsChangedListener = this::handlePortConnectionsChanged;
    private final ChangeListener<Object> portActivationChangedListener = this::handlePortActivationChanged;
    private final ChangeListener<Object> portCoordinatesChangedListener = this::handlePortCoordinatesChanged;
    private final ChangeListener<Object> startPortDataChangedListener = this::handleStartPortDataChanged;

    public Port(String name, Block parent, Type portType, Class<?> type) {
        tip = new Tooltip();
        Tooltip.install(this, tip);
        tip.textProperty().bind(this.nameProperty());

        this.parentBlock = parent;
        this.dataType = type;
        this.portType = portType;
        this.setName(name);

        if (portType == Type.IN) {
            index = parent.inPorts.size();
        } else {
            index = parent.outPorts.size();
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

    public void handlePortClickedForNewConnection(MouseEvent event) {
        if (event.isStillSincePress()) {
            parentBlock.workspaceController.initiateConnection(Port.this);

        }
        event.consume();
    }

    public void handlePortPressed(MouseEvent event) {
        event.consume();
    }

    private void handlePortConnectionsChanged(Change<? extends Connection> change) {
        if (connectedConnections.size() == 0) {
            setActive(false);
        } else {
            setActive(true);
        }
    }

    private void calcOrigin() {
        Point2D centerInScene = localToScene(getWidth() / 2, getHeight() / 2);
        Point2D centerInLocal = parentBlock.workspaceController.getView().sceneToLocal(centerInScene);

        centerXProperty.set(centerInLocal.getX());
        centerYProperty.set(centerInLocal.getY());
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

                for (Connection connection : connectedConnections) {

                    //Cast all primitive dataType to String if this port dataType is String
                    Port startPort = connection.getStartPort();
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

            } else if (connectedConnections.size() > 0) {
                System.out.println("Data Received: " + value);

                //Cast all primitive dataType to String if this port dataType is String
                Port startPort = connectedConnections.get(0).getStartPort();
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

            } else {
                data.set(null);
            }
        } else {
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

        for (Connection connection : connectedConnections) {

        }

        connectedConnections.clear();
    }
}

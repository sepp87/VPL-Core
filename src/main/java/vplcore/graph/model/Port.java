package vplcore.graph.model;

import javafx.scene.layout.VBox;
import java.util.*;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
//    public BindingPoint origin;
    public int index;

    public Port(String name, Block parent, Type portType, Class type) {
        Tooltip tip = new Tooltip();
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
        connectedConnections.addListener(this::handle_ConnectionChange);

        setOnMouseClicked(createConnectionHandler);
        setOnMousePressed(consumePressHandler);
        setOnMouseDragged(this::port_MouseDrag);

        active.addListener(this::handle_Active);

        parentBlock.layoutXProperty().addListener(coordinatesChangeListener);
        parentBlock.layoutYProperty().addListener(coordinatesChangeListener);
        boundsInParentProperty().addListener(coordinatesChangeListener);
    }

    private final EventHandler<MouseEvent> createConnectionHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (event.isStillSincePress()) {
                parentBlock.workspace.portConnector.createConnection(Port.this);
            }
            event.consume();
        }
    };
    private final EventHandler<MouseEvent> consumePressHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            event.consume();
        }
    };

    private void handle_ConnectionChange(ListChangeListener.Change change) {
        if (connectedConnections.size() == 0) {
            setActive(false);
        } else {
            setActive(true);
        }
    }

    private void calcOrigin() {
        Point2D centerInScene = localToScene(getWidth() / 2, getHeight() / 2);
        Point2D centerInLocal = parentBlock.workspace.sceneToLocal(centerInScene);

        centerXProperty.set(centerInLocal.getX());
        centerYProperty.set(centerInLocal.getY());
    }

    ChangeListener<Object> coordinatesChangeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            calcOrigin();
        }
    };

    /**
     * @TODO CHANGE FROM ORIGINAL CODE Consume event to prevent block from
     * moving around.
     *
     * @param e
     */
    private void port_MouseDrag(MouseEvent e) {
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

    //Double point operators do NOT work when trying to remove listeners
    //USE THIS OTHERWISE THERE WILL BE MEMORY LEAKING
    ChangeListener<Object> startPort_DataChangeListener = new ChangeListener<>() {

        @Override
        public void changed(ObservableValue obj, Object oldVal, Object newVal) {
            calculateData(newVal);
        }
    };

//    OBSOLETE CODE, SINCE dataChanged IS ALREADY THIS AND IS MONITORED?
//    public void OnDataChanged() {
//        if (DataChanged != null) {
//            DataChanged(this, new EventArgs());
//        }
//    }
    public void calculateData() {
        calculateData(null);
    }

    public void calculateData(Object value) {

//        boolean fxThread = Thread.currentThread().getName().equals("JavaFX Application Thread");
//        if (!fxThread) {
//            System.out.println(this.parentBlock.getName());
//        }
        if (portType == Type.IN) {

            if (multiDockAllowed && connectedConnections.size() > 1) {

                dataType.cast(new Object());
                List listOfLists = new ArrayList<>();

//                var listType = typeof(List < >).MakeGenericType(new Type[]{DataType});
//                IList list = (IList) Activator.CreateInstance(listType);
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

    private void handle_Active(Object obj, Object oldVal, Object newVal) {
        if (isActive()) {
            getStyleClass().remove("port");
            getStyleClass().add("port-active");
        } else {
            getStyleClass().remove("port-active");
            getStyleClass().add("port");
        }
    }

}

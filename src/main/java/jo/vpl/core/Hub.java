package jo.vpl.core;

import java.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import jo.vpl.xml.HubTag;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
public abstract class Hub extends VplElement {

    public UUID uuid;
    public Pane inPortBox;
    public Pane outPortBox;
    public List<Port> inPorts;
    public List<Port> outPorts;
    public List<Region> controls;
    public GridPane contentGrid;
    public GridPane mainContentGrid;
    private Boolean resizable = false;

    public Point2D oldMousePosition;

    public Hub(VplControl vplControl) {
        super(vplControl);
        uuid = UUID.randomUUID();

        inPorts = new ArrayList();
        outPorts = new ArrayList();
        controls = new ArrayList();

        //Content Grid is the actual hub box without the buttons on top etc.
        contentGrid = new GridPane();
//        contentGrid.getStyleClass().add("hub");

        contentGrid.setAlignment(Pos.CENTER);
        contentGrid.addEventFilter(MouseEvent.MOUSE_ENTERED, onMouseEnterEventHandler);
        contentGrid.addEventFilter(MouseEvent.MOUSE_EXITED, onMouseExitEventHandler);
        contentGrid.setOnMousePressed(this::handle_MousePress);
        selected.addListener(selectChangeListener);

        if (true) {
            VBox in = new VBox();
            VBox out = new VBox();

            in.setAlignment(Pos.CENTER);
            out.setAlignment(Pos.CENTER);

            inPortBox = in;
            outPortBox = out;

            contentGrid.add(inPortBox, 0, 1);
            contentGrid.add(outPortBox, 2, 1);

            ColumnConstraints column1 = new ColumnConstraints();
            ColumnConstraints column2 = new ColumnConstraints();
            ColumnConstraints column3 = new ColumnConstraints();

            column1.setHgrow(Priority.NEVER);
            column2.setHgrow(Priority.ALWAYS);
            column3.setHgrow(Priority.NEVER);
            column3.setHalignment(HPos.RIGHT);

            contentGrid.getColumnConstraints().addAll(column1, column2, column3);
        } else {
            HBox in = new HBox();
            HBox out = new HBox();

            in.setAlignment(Pos.CENTER);
            out.setAlignment(Pos.CENTER);

            inPortBox = in;
            outPortBox = out;

            contentGrid.add(inPortBox, 1, 0);
            contentGrid.add(outPortBox, 1, 3);
        }

        contentGrid.getStyleClass().add("hub");
        inPortBox.getStyleClass().add("in-port-box");
        outPortBox.getStyleClass().add("out-port-box");

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        row2.setVgrow(Priority.ALWAYS);
        row3.setVgrow(Priority.NEVER);
        row4.setVgrow(Priority.NEVER);

        contentGrid.getRowConstraints().addAll(row1, row2, row3, row4);

        //Main content grid is -> the center for controls
        mainContentGrid = new GridPane();
        contentGrid.add(mainContentGrid, 1, 1);

        //Main content grid constraints to make content grow
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        column.setHalignment(HPos.CENTER);
        mainContentGrid.getColumnConstraints().addAll(column);

        super.add(contentGrid, 1, 1);
    }

    public void setResizable(boolean resizable) {
        if (resizable) {
            resizeButton = new HubButton(IconType.FA_PLUS_SQUARE_O);
            contentGrid.add(resizeButton, 2, 3);

            resizeButton.setOnMousePressed(this::resizeButton_MousePress);
            resizeButton.setOnMouseDragged(this::resizeButton_MouseDrag);
        }
    }

    private void resizeButton_MousePress(MouseEvent e) {
        oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
    }

    private void resizeButton_MouseDrag(MouseEvent e) {
        double scale = hostCanvas.getScale();

        double deltaX = (e.getSceneX() - oldMousePosition.getX()) / scale;
        double deltaY = (e.getSceneY() - oldMousePosition.getY()) / scale;

        contentGrid.setMinWidth(contentGrid.getWidth() + deltaX);
        contentGrid.setMinHeight(contentGrid.getHeight() + deltaY);

        oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
    }

    /**
     * Event handler for selection of hubs and possible followed up dragging of
     * them by the user.
     *
     * @param e
     */
    private void handle_MousePress(MouseEvent e) {

        if (hostCanvas.selectedHubSet.contains(this)) {
            if (e.isControlDown()) {
                // Remove this node from selection
                hostCanvas.selectedHubSet.remove(this);
                setSelected(false);
            } else {
                // Subscribe multiselection to MouseMove event
                for (Hub hub : hostCanvas.selectedHubSet) {
                    hub.setOnMouseDragged(hub::handle_MouseDrag);

                    hub.oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
                }
            }
        } else {
            if (e.isControlDown()) {
                // add this node to selection
                hostCanvas.selectedHubSet.add(this);

                setSelected(true);
            } else {
                // Deselect all hubs that are selected
                for (Hub hub : hostCanvas.selectedHubSet) {
                    hub.setSelected(false);
                }

                hostCanvas.selectedHubSet.clear();
                hostCanvas.selectedHubSet.add(this);
                // Select this hub as selected
                setSelected(true);
                for (Hub hub : hostCanvas.selectedHubSet) {
                    //Add mouse dragged event handler so the hub will move
                    //when the user starts dragging it
                    this.setOnMouseDragged(hub::handle_MouseDrag);

                    //Get mouse position so there is a value to calculate 
                    //in the mouse dragged event
                    hub.oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
                }
            }
        }
        e.consume();
    }

    public void handle_MouseDrag(MouseEvent e) {

        double scale = hostCanvas.getScale();

        double deltaX = (e.getSceneX() - oldMousePosition.getX()) / scale;
        double deltaY = (e.getSceneY() - oldMousePosition.getY()) / scale;

        for (Hub hub : hostCanvas.selectedHubSet) {

            hub.setLayoutX(hub.getLayoutX() + deltaX);
            hub.setLayoutY(hub.getLayoutY() + deltaY);
        }

        oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
    }

    /**
     * Add a port to the hub, multiple connections are not allowed by default
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @return the Port
     */
    public Port addInPortToHub(String name, Class type) {
        return addInPortToHub(name, type, false);
    }

    /**
     * Add a port to the hub
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @param multiDockAllowed if multiple connections are allowed
     * @return the Port
     */
    public Port addInPortToHub(String name, Class type, boolean multiDockAllowed) {
        Port port = new Port(name, this, PortTypes.IN, type);
        port.multiDockAllowed = multiDockAllowed;
        inPortBox.getChildren().add(port);
        port.dataProperty().addListener(port_DataChangeListener);
        inPorts.add(port);
        return port;
    }

    /**
     * Remove a port from the hub
     *
     * @param port the port to remove
     */
    public void removeInPortFromHub(Port port) {
        for (Connection connector : port.connectedConnections) {
            connector.removeFromCanvas();
        }
        inPortBox.getChildren().remove(port);
        port.dataProperty().removeListener(port_DataChangeListener);
        inPorts.remove(port);
    }

    //Double point operators do NOT work when trying to remove listeners
    //USE THIS OTHERWISE THERE WILL BE MEMORY LEAKING
    ChangeListener port_DataChangeListener = new ChangeListener() {

        @Override
        public void changed(ObservableValue obj, Object oldVal, Object newVal) {
            //        try {
//            if (AutoCheckBox.IsChecked != null && (bool) AutoCheckBox.IsChecked) {

            calculate();
            //            }
//            HasError = false;
//            TopComment.Visibility = Visibility.Hidden;
//        } catch (Exception ex) {
//            HasError = true;
//            TopComment.Text = ex.ToString();
//            TopComment.Visibility = Visibility.Visible;
//        }
        }
    };

    /**
     * Add a port to the hub, multiple outgoing connections are allowed
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @return the Port
     */
    public Port addOutPortToHub(String name, Class type) {
        Port port = new Port(name, this, PortTypes.OUT, type);
        port.multiDockAllowed = true;
        outPortBox.getChildren().add(port);
        outPorts.add(port);
        return port;
    }

    /**
     * Add control to the hub. A control extends region so it can be a layout,
     * but also a simple control like a button.
     *
     * @param control the control to add
     */
    public void addControlToHub(Region control) {
        mainContentGrid.add(control, 0, mainContentGrid.getChildren().size());
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        mainContentGrid.getRowConstraints().add(row);
        controls.add(control);
    }

    @Override
    public void binButton_MouseClick(MouseEvent e) {
        delete();
    }

    /**
     * Remove this hub from the host canvas
     */
    public void delete() {
        hostCanvas.hubSet.remove(this);
        super.delete();
    }

    /**
     * Called when a new connection is incoming. Ideal for forwarding a data type
     * to an out port e.g. hubs operating on collections. Its removed counterpart
     * is used to set the data type of the out port back to its initial state. 
     * Only called when multi dock is not allowed!
     *
     * @param source port the connection was added to
     * @param incoming port which sends the data
     */
    protected void handle_IncomingConnectionAdded(Port source, Port incoming) {

    }

    /**
     * Called when an incoming connection is removed. Ideal for forwarding a data type
     * to an out port e.g. hubs operating on collections. Its removed counterpart
     * is used to set the data type of the out port back to its initial state.
     * Only called when multi dock is not allowed!
     *
     * @param source port the connection was removed from
     */
    protected void handle_IncomingConnectionRemoved(Port source) {

    }

    public abstract void calculate();

    protected abstract Hub clone();

    public void serialize(HubTag xmlTag) {
//        xmlTag.setType(getClass().getName());
        xmlTag.setType(this.getClass().getAnnotation(HubInfo.class).name());
        xmlTag.setUUID(uuid.toString());
        xmlTag.setX(getLayoutX());
        xmlTag.setY(getLayoutY());
    }

    public void deserialize(HubTag xmlTag) {
        uuid = UUID.fromString(xmlTag.getUUID());
        setLayoutX(xmlTag.getX());
        setLayoutY(xmlTag.getY());
        List<Hub> hubs = new ArrayList<>();
    }

    public <type extends Hub> void testList(type... hubs) {

    }

    public static Bounds getBoundingBoxOfHubs(Collection<? extends Hub> hubs) {
        if (hubs == null || hubs.isEmpty()) {
            return null;
        }
        double minLeft = Double.MAX_VALUE;
        double minTop = Double.MAX_VALUE;
        double maxLeft = Double.MIN_VALUE;
        double maxTop = Double.MIN_VALUE;

        for (Hub hub : hubs) {
            if (hub.getLayoutX() < minLeft) {
                minLeft = hub.getLayoutX();
            }
            if (hub.getLayoutY() < minTop) {
                minTop = hub.getLayoutY();
            }

            if ((hub.getLayoutX() + hub.getWidth()) > maxLeft) {
                maxLeft = hub.getLayoutX() + hub.getWidth();
            }
            if ((hub.getLayoutY() + hub.getHeight()) > maxTop) {
                maxTop = hub.getLayoutY() + hub.getHeight();
            }
        }

        return new BoundingBox(minLeft, minTop, maxLeft - minLeft, maxTop - minTop);
    }

    private final EventHandler<MouseEvent> onMouseEnterEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            Hub.this.setActive(true);
            Hub.this.updateStyle();
        }
    };
    private final EventHandler<MouseEvent> onMouseExitEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            //Change focus on exit to host canvas so controls do not interrupt key events
            Hub.this.hostCanvas.requestFocus();
            Hub.this.setActive(false);
            Hub.this.updateStyle();
        }
    };
    private final EventHandler<MouseEvent> onMousePressEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {

        }
    };

    private final ChangeListener<Boolean> selectChangeListener = new ChangeListener<Boolean>() {

        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
            updateStyle();
        }
    };

    public void updateStyle() {
//        System.out.println(this.isPressed());
//        System.out.println(this.isSelected());
        if (isSelected()) {
//          contentGrid.getStyleClass().clear();
            contentGrid.getStyleClass().add("hub-selected");
        } else {
            contentGrid.getStyleClass().clear();
            contentGrid.getStyleClass().add("hub");
        }
    }

    /**
     * Pick yourself a wonderfully awesome icon
     *
     * @param type
     * @return
     */
    public Label getAwesomeIcon(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.getStyleClass().add("hub-awesome-icon");
        return label;
    }
}

enum PortTypes {

    IN,
    OUT
}

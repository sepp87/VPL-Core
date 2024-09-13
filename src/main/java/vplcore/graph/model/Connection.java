package vplcore.graph.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import jo.vpl.xml.ConnectionTag;
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public class Connection {

    private final ChangeListener<Object> blockDeletedListener = this::handleBlockDeleted;
    private final ChangeListener<Object> portCoordinatesChangedListener = this::handlePortCoordinatesChanged;

    public final static double SNAPPING_WIDTH = 22;
    public final Port startPort;
    public final Port endPort;
    public Workspace workspace;
    public CubicCurve connectionCurve;
    public CubicCurve snappingCurve;

    public final DoubleProperty startBezierXProperty = new SimpleDoubleProperty();
    public final DoubleProperty endBezierXProperty = new SimpleDoubleProperty();

    /**
     * A connection contains a reference to an in- and outport. Its visual
     * representation is a cubic(bezier) curve between two ports.
     *
     * @param workspace
     * @param startPort the OUT port [ ]<
     * @param endPort the IN port >[ ]
     */
    public Connection(Workspace workspace, Port startPort, Port endPort) {
        this.workspace = workspace;

        this.startPort = startPort;
        this.endPort = endPort;

        this.startPort.setActive(true);
        this.endPort.setActive(true);

        startPort.connectedConnections.add(this);
        endPort.connectedConnections.add(this);

        //A single incoming connection was made, handle it in the block
        //to forward incoming data type to out port e.g. in getFirstItemOfList
        endPort.parentBlock.handleIncomingConnectionAdded(endPort, startPort);

        endPort.calculateData(startPort.getData());

        /**
         * @TODO::NEW CHANGE FROM ORIGINAL CODE Check if connection already
         * exist within the connectionCollection
         */
        connectionCurve = createCurve();
        connectionCurve.getStyleClass().add("connection");

        snappingCurve = createCurve();
        snappingCurve.setFill(null);
        snappingCurve.setStrokeWidth(SNAPPING_WIDTH);
        snappingCurve.setStroke(Color.TRANSPARENT);
        snappingCurve.setUserData(this);

        workspace.getChildren().add(0, snappingCurve);
        workspace.getChildren().add(0, connectionCurve);
        addChangeListeners();

    }

    private void addChangeListeners() {
        //Use of 'real listeners' that support removeListener()
        startPort.dataProperty().addListener(endPort.getStartPortDataChangedListener());

        // new listeners
        startPort.centerXProperty.addListener(portCoordinatesChangedListener);
        startPort.centerYProperty.addListener(portCoordinatesChangedListener);
        endPort.centerXProperty.addListener(portCoordinatesChangedListener);
        endPort.centerYProperty.addListener(portCoordinatesChangedListener);

        startPort.parentBlock.deleted.addListener(blockDeletedListener);
        endPort.parentBlock.deleted.addListener(blockDeletedListener);

        snappingCurve.addEventHandler(MouseEvent.MOUSE_MOVED, workspace.portDisconnector.movedOnSnappingCurveHandler);
        snappingCurve.addEventHandler(MouseEvent.MOUSE_EXITED, workspace.portDisconnector.exitedSnappingCurveHandler);
        snappingCurve.addEventHandler(MouseEvent.MOUSE_ENTERED, workspace.portDisconnector.enteredSnappingCurveHandler);

    }

    public void handlePortCoordinatesChanged(ObservableValue ov, Object t, Object t1) {
        calculateBezierPoints();
    }

    private CubicCurve createCurve() {
        calculateBezierPoints();

        CubicCurve curve = new CubicCurve();
        curve.controlX1Property().bind(startBezierXProperty);
        curve.controlY1Property().bind(startPort.centerYProperty);
        curve.startXProperty().bind(startPort.centerXProperty);
        curve.startYProperty().bind(startPort.centerYProperty);

        curve.controlX2Property().bind(endBezierXProperty);
        curve.controlY2Property().bind(endPort.centerYProperty);
        curve.endXProperty().bind(endPort.centerXProperty);
        curve.endYProperty().bind(endPort.centerYProperty);

        return curve;
    }

    private void calculateBezierPoints() {
        Double dX = endPort.centerXProperty.get() - startPort.centerXProperty.get();
        Double dY = endPort.centerYProperty.get() - startPort.centerYProperty.get();
        Point2D vector = new Point2D(dX, dY);
        double distance = vector.magnitude() / 2;

        startBezierXProperty.set(startPort.centerXProperty.get() + distance);
        endBezierXProperty.set(endPort.centerXProperty.get() - distance);

    }

    public Port getStartPort() {
        return startPort;
    }

    public Port getEndPort() {
        return endPort;
    }

    public void serialize(ConnectionTag xmlTag) {
        xmlTag.setStartBlock(startPort.parentBlock.uuid.toString());
        xmlTag.setStartIndex(startPort.parentBlock.outPorts.indexOf(startPort));
        xmlTag.setEndBlock(endPort.parentBlock.uuid.toString());
        xmlTag.setEndIndex(endPort.parentBlock.inPorts.indexOf(endPort));
    }

    private void handleBlockDeleted(ObservableValue arg0, Object arg1, Object arg2) {
        removeFromCanvas();
        removeChangeListeners();

        startPort.connectedConnections.remove(Connection.this);
        endPort.connectedConnections.remove(Connection.this);

        //Deactivate ports if they have no more connections
        if (startPort.connectedConnections.isEmpty()) {
            startPort.setActive(false);
        }

        if (endPort.connectedConnections.isEmpty()) {
            endPort.setActive(false);
        }

        endPort.calculateData();
    }

    public void removeFromCanvas() {
        workspace.getChildren().remove(connectionCurve);
        workspace.getChildren().remove(snappingCurve);
        unbindCurve(connectionCurve);
        unbindCurve(snappingCurve);
        workspace.connectionSet.remove(this);
        if (!endPort.multiDockAllowed) {
            endPort.parentBlock.handleIncomingConnectionRemoved(endPort);
        }
    }

    private void unbindCurve(CubicCurve curve) {
        curve.controlX1Property().unbind();
        curve.controlY1Property().unbind();
        curve.startXProperty().unbind();
        curve.startYProperty().unbind();

        curve.controlX2Property().unbind();
        curve.controlY2Property().unbind();
        curve.endXProperty().unbind();
        curve.endYProperty().unbind();
    }

    private void removeChangeListeners() {
        startPort.dataProperty().removeListener(endPort.getStartPortDataChangedListener());
        startPort.parentBlock.deleted.removeListener(blockDeletedListener);
        endPort.parentBlock.deleted.removeListener(blockDeletedListener);

        startPort.centerXProperty.removeListener(portCoordinatesChangedListener);
        startPort.centerYProperty.removeListener(portCoordinatesChangedListener);
        endPort.centerXProperty.removeListener(portCoordinatesChangedListener);
        endPort.centerYProperty.removeListener(portCoordinatesChangedListener);

        snappingCurve.removeEventHandler(MouseEvent.MOUSE_MOVED, workspace.portDisconnector.movedOnSnappingCurveHandler);
        snappingCurve.removeEventHandler(MouseEvent.MOUSE_EXITED, workspace.portDisconnector.exitedSnappingCurveHandler);
        snappingCurve.removeEventHandler(MouseEvent.MOUSE_ENTERED, workspace.portDisconnector.enteredSnappingCurveHandler);
    }
}

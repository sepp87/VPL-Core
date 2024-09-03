package vplcore.graph.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.CubicCurve;
import vplcore.workspace.Workspace;
import jo.vpl.xml.ConnectionTag;
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public class Connection {

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
        endPort.parentBlock.handle_IncomingConnectionAdded(endPort, startPort);

        endPort.calculateData(startPort.getData());

        /**
         * @TODO::NEW CHANGE FROM ORIGINAL CODE Check if connection already
         * exist within the connectionCollection
         */
        connectionCurve = createCurve();
        connectionCurve.getStyleClass().add("connection");

        snappingCurve = createCurve();
        snappingCurve.setFill(null);
        snappingCurve.setStrokeWidth(50);
        snappingCurve.setStroke(Color.TRANSPARENT);
        snappingCurve.setUserData(this);

        workspace.getChildren().add(0, snappingCurve);
        workspace.getChildren().add(0, connectionCurve);
        addChangeListeners();

    }

    private void addChangeListeners() {
        //Use of 'real listeners' that support removeListener()
        startPort.dataProperty().addListener(endPort.startPort_DataChangeListener);

        // new listeners
        startPort.centerXProperty.addListener(coordinatesChangeListener);
        startPort.centerYProperty.addListener(coordinatesChangeListener);
        endPort.centerXProperty.addListener(coordinatesChangeListener);
        endPort.centerYProperty.addListener(coordinatesChangeListener);

        startPort.parentBlock.deleted.addListener(block_DeletedInBlockSetListener);
        endPort.parentBlock.deleted.addListener(block_DeletedInBlockSetListener);

//        snappingCurve.setOnMouseMoved(workspace.portDisconnector.movedOnSnappingCurveHandler);
//        snappingCurve.setOnMouseExited(workspace.portDisconnector.exitedSnappingCurveHandler);
//        snappingCurve.setOnMouseEntered(workspace.portDisconnector.enteredSnappingCurveHandler);
        snappingCurve.addEventFilter(MouseEvent.MOUSE_MOVED, workspace.portDisconnector.movedOnSnappingCurveHandler);
        snappingCurve.addEventFilter(MouseEvent.MOUSE_EXITED, workspace.portDisconnector.exitedSnappingCurveHandler);
        snappingCurve.addEventFilter(MouseEvent.MOUSE_ENTERED, workspace.portDisconnector.enteredSnappingCurveHandler);

    }

    private final ChangeListener<Object> coordinatesChangeListener = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {
            calculateBezierPoints();
        }
    };

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

    public void removeFromCanvas() {
        workspace.getChildren().remove(connectionCurve);
        workspace.getChildren().remove(snappingCurve);
        unbindCurve(connectionCurve);
        unbindCurve(snappingCurve);
        workspace.connectionSet.remove(this);
        if (!endPort.multiDockAllowed) {
            endPort.parentBlock.handle_IncomingConnectionRemoved(endPort);
        }
    }

    public void serialize(ConnectionTag xmlTag) {
        xmlTag.setStartBlock(startPort.parentBlock.uuid.toString());
        xmlTag.setStartIndex(startPort.parentBlock.outPorts.indexOf(startPort));
        xmlTag.setEndBlock(endPort.parentBlock.uuid.toString());
        xmlTag.setEndIndex(endPort.parentBlock.inPorts.indexOf(endPort));
    }

    PropertyChangeListener origin_PropertyChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            calculateBezierPoints();
        }
    };

    ChangeListener block_DeletedInBlockSetListener = new ChangeListener() {

        @Override
        public void changed(ObservableValue arg0, Object arg1, Object arg2) {
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
    };

    private void removeChangeListeners() {

        startPort.dataProperty().removeListener(endPort.startPort_DataChangeListener);
        //Remove listeners although with object references gone they ought to be collected automatically
        startPort.parentBlock.deleted.removeListener(block_DeletedInBlockSetListener);
        endPort.parentBlock.deleted.removeListener(block_DeletedInBlockSetListener);

        // new listeners
        startPort.centerXProperty.removeListener(coordinatesChangeListener);
        startPort.centerYProperty.removeListener(coordinatesChangeListener);
        endPort.centerXProperty.removeListener(coordinatesChangeListener);
        endPort.centerYProperty.removeListener(coordinatesChangeListener);
    }
}

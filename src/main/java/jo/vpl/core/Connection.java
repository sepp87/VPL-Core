package jo.vpl.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.shape.CubicCurve;
import javax.vecmath.Vector2d;
import jo.vpl.xml.ConnectionTag;

/**
 *
 * @author JoostMeulenkamp
 */
public class Connection {

    public final Port startPort;
    public final Port endPort;
    public VPLControl hostCanvas;
    public CubicCurve curve;
    private final BindingPoint startBezierPoint;
    private final BindingPoint endBezierPoint;

    /**
     * A connection contains a reference to an in- and outport. Its visual
     * representation is a cubic(bezier) curve between two ports.
     *
     * @param hostCanvas
     * @param startPort the OUT port [ ]<
     * @param endPort the IN port >[ ]
     */
    public Connection(VPLControl hostCanvas, Port sPort, Port ePort) {
        this.hostCanvas = hostCanvas;

        this.startPort = sPort;
        this.endPort = ePort;

        this.startPort.setActive(true);
        this.endPort.setActive(true);

        startBezierPoint = new BindingPoint(startPort.origin.getX(), startPort.origin.getY());
        endBezierPoint = new BindingPoint(endPort.origin.getX(), endPort.origin.getY());

        //Use of 'real listeners' that support removeListener()
        startPort.dataProperty().addListener(endPort.startPort_DataChangeListener);

        startPort.origin.propertyChanged.add(origin_PropertyChangeListener);
        endPort.origin.propertyChanged.add(origin_PropertyChangeListener);

        startPort.parentHub.eventBlaster.add(origin_PropertyChangeListener);
        endPort.parentHub.eventBlaster.add(origin_PropertyChangeListener);

        startPort.parentHub.deleted.addListener(hub_DeletedInHubSetListener);
        endPort.parentHub.deleted.addListener(hub_DeletedInHubSetListener);

        startPort.connectedConnections.add(this);
        endPort.connectedConnections.add(this);

        endPort.calculateData(startPort.getData());

        /**
         * @TODO::NEW CHANGE FROM ORIGINAL CODE
         * Check if connection already exist within the connectionCollection
         */
        defineCurve();

        hostCanvas.getChildren().add(0, curve);
    }

    private void defineCurve() {
        calculateBezierPoints();

        curve = new CubicCurve();
        curve.startXProperty().bind(startPort.origin.x());
        curve.startYProperty().bind(startPort.origin.y());
        curve.controlX1Property().bind(startBezierPoint.x());
        curve.controlY1Property().bind(startPort.origin.y());

        curve.controlX2Property().bind(endBezierPoint.x());
        curve.controlY2Property().bind(endPort.origin.y());
        curve.endXProperty().bind(endPort.origin.x());
        curve.endYProperty().bind(endPort.origin.y());

        curve.getStyleClass().add("connection");

    }

    private void calculateBezierPoints() {
        Double distance = new Vector2d((endPort.origin.getX() - startPort.origin.getX()),
                (endPort.origin.getY() - startPort.origin.getY())).length() / 2;

        startBezierPoint.setX(startPort.origin.getX() + distance);
        endBezierPoint.setX(endPort.origin.getX() - distance);
    }

    public Port getStartPort() {
        return startPort;
    }

    public Port getEndPort() {
        return endPort;
    }

    public void removeFromCanvas() {

        hostCanvas.getChildren().remove(curve);
        hostCanvas.connectionSet.remove(this);

        startPort.dataProperty().removeListener(endPort.startPort_DataChangeListener);
    }

    public void serialize(ConnectionTag xmlTag) {
        xmlTag.setStartHub(startPort.parentHub.uuid.toString());
        xmlTag.setStartIndex(startPort.parentHub.outPorts.indexOf(startPort));
        xmlTag.setEndHub(endPort.parentHub.uuid.toString());
        xmlTag.setEndIndex(endPort.parentHub.inPorts.indexOf(endPort));
    }

    PropertyChangeListener origin_PropertyChangeListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            calculateBezierPoints();
        }
    };

    ChangeListener hub_DeletedInHubSetListener = new ChangeListener() {

        @Override
        public void changed(ObservableValue arg0, Object arg1, Object arg2) {
            removeFromCanvas();

            startPort.connectedConnections.remove(Connection.this);
            endPort.connectedConnections.remove(Connection.this);

            //Remove listeners although with object references gone they ought to be collected automatically
            startPort.parentHub.deleted.removeListener(hub_DeletedInHubSetListener);
            endPort.parentHub.deleted.removeListener(hub_DeletedInHubSetListener);

            startPort.origin.propertyChanged.remove(origin_PropertyChangeListener);
            endPort.origin.propertyChanged.remove(origin_PropertyChangeListener);

            startPort.parentHub.eventBlaster.remove(origin_PropertyChangeListener);
            endPort.parentHub.eventBlaster.remove(origin_PropertyChangeListener);

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
}

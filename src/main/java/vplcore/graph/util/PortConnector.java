package vplcore.graph.util;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class PortConnector {

    public enum State {
        NOT_CONNECTING,
        IS_CONNECTING
    }

    private Workspace workspace;
    public Line tempLine;
    public Port tempStartPort;
    public State state = State.NOT_CONNECTING;

    public PortConnector(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // Do NOT use MOUSE_PRESSED or MOUSE_RELEASED otherwise click on port to start connecting and a consecutive click in empty space causes mouseMode to stick in SELECTING
    }

    private final EventHandler<MouseEvent> mouseMovedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            switch (state) {
                case NOT_CONNECTING:
                    clearTempLine();
                    break;

                case IS_CONNECTING:
                    if (tempLine == null) {
                        tempLine = new Line();
                        tempLine.getStyleClass().add("temp-line");
                        workspace.getChildren().add(0, tempLine);
                    }

                    tempLine.startXProperty().bind(tempStartPort.centerXProperty);
                    tempLine.startYProperty().bind(tempStartPort.centerYProperty);
                    tempLine.setEndX(workspace.sceneToLocal(event.getSceneX(), event.getSceneY()).getX());
                    tempLine.setEndY(workspace.sceneToLocal(event.getSceneX(), event.getSceneY()).getY());

                    break;

                default:
                    throw new IndexOutOfBoundsException("Argument out of range.");

            }
        }
    };

    public void createConnection(Port port) {

        switch (state) {
            case NOT_CONNECTING:
                tempStartPort = port;
                state = State.IS_CONNECTING;
                break;

            case IS_CONNECTING:
                /**
                 * Check if the data type from the sending port is the same or a
                 * sub class of the receiving port.
                 */
                if (((TypeExtensions.isCastableTo(tempStartPort.dataType, port.dataType)
                        && workspace.typeSensitive && port.portType == Port.Type.IN)
                        || (TypeExtensions.isCastableTo(port.dataType, tempStartPort.dataType)
                        && workspace.typeSensitive && port.portType == Port.Type.OUT)
                        // IN case dataProperty type does not matter
                        || (!workspace.typeSensitive))
                        // Cannot be the same port type; IN > OUT or OUT > IN
                        && port.portType != tempStartPort.portType
                        // Cannot be the same block
                        && !port.parentBlock.equals(tempStartPort.parentBlock)) {

                    Connection connection;

                    /**
                     * Make a new connection and remove all the existing
                     * connections Where is multi connect?
                     */
                    if (port.portType == Port.Type.OUT) {
                        if (!tempStartPort.connectedConnections.isEmpty()) {

                            if (!tempStartPort.multiDockAllowed) {
                                for (Connection c : tempStartPort.connectedConnections) {
                                    c.removeFromCanvas();
                                }
                            }
                        }
                        connection = new Connection(workspace, port, tempStartPort);

                    } else {
                        if (!port.connectedConnections.isEmpty()) {

                            if (!port.multiDockAllowed) {
                                for (Connection c : port.connectedConnections) {
                                    c.removeFromCanvas();
                                    c.startPort.connectedConnections.remove(c);
                                }
                                port.connectedConnections.clear();
                            }
                        }
                        connection = new Connection(workspace, tempStartPort, port);
                    }
                    workspace.connectionSet.add(connection);

                }
                /**
                 * Return values back to default state in which no connection is
                 * being made.
                 */
                state = State.NOT_CONNECTING;
                clearTempLine();
                break;

        }
    }

    private final EventHandler<MouseEvent> mouseClickedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            Node node = event.getPickResult().getIntersectedNode();
            boolean onPort = workspace.checkParents(node, Port.class);
            boolean isConnecting = state == State.IS_CONNECTING;
            boolean isPrimaryButton = event.getButton() == MouseButton.PRIMARY;
            if (isPrimaryButton && isConnecting && !onPort) {
                state = State.NOT_CONNECTING;
                clearTempLine();
            }
        }
    };

    public void clearTempLine() {
        workspace.getChildren().remove(tempLine);
        tempLine = null;
    }
}

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
 * @author Joost
 */
public class PreConnection extends Line {

    private final Workspace workspace;
    private final Port startPort;

    private final EventHandler<MouseEvent> mouseMovedHandler = this::handleMouseMoved;
    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;

    public PreConnection(Workspace workspace, Port startPort) {
        this.workspace = workspace;
        this.startPort = startPort;

        getStyleClass().add("temp-line");

        startXProperty().bind(startPort.centerXProperty);
        startYProperty().bind(startPort.centerYProperty);
        setEndX(startPort.centerXProperty.get());
        setEndY(startPort.centerYProperty.get());

        workspace.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // Do NOT use MOUSE_PRESSED or MOUSE_RELEASED otherwise click on port to start connecting and a consecutive click in empty space causes mouseMode to stick in SELECTING
    }

    private void handleMouseMoved(MouseEvent event) {
        setEndX(workspace.sceneToLocal(event.getSceneX(), event.getSceneY()).getX());
        setEndY(workspace.sceneToLocal(event.getSceneX(), event.getSceneY()).getY());

    }

    private void handleMouseClicked(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onPort = intersectedNode instanceof Port;
        boolean isPrimaryButton = event.getButton() == MouseButton.PRIMARY;
        if (isPrimaryButton && onPort) {
            createConnection((Port) intersectedNode);

        } else {
            remove();
        }
        event.consume();
    }

    private void createConnection(Port endPort) {

        /**
         * Check if the data type from the sending port is the same or a sub
         * class of the receiving port.
         */
        if (((TypeExtensions.isCastableTo(startPort.dataType, endPort.dataType)
                && workspace.typeSensitive && endPort.portType == Port.Type.IN)
                || (TypeExtensions.isCastableTo(endPort.dataType, startPort.dataType)
                && workspace.typeSensitive && endPort.portType == Port.Type.OUT)
                // IN case dataProperty type does not matter
                || (!workspace.typeSensitive))
                // Cannot be the same port type; IN > OUT or OUT > IN
                && endPort.portType != startPort.portType
                // Cannot be the same block
                && !endPort.parentBlock.equals(startPort.parentBlock)) {

            Connection connection;

            /**
             * Make a new connection and remove all the existing connections
             * Where is multi connect?
             */
            if (endPort.portType == Port.Type.OUT) {
                if (!startPort.connectedConnections.isEmpty()) {

                    if (!startPort.multiDockAllowed) {
                        for (Connection c : startPort.connectedConnections) {
                            c.removeFromCanvas();
                        }
                    }
                }
                connection = new Connection(workspace, endPort, startPort);

            } else {
                if (!endPort.connectedConnections.isEmpty()) {

                    if (!endPort.multiDockAllowed) {
                        for (Connection c : endPort.connectedConnections) {
                            c.removeFromCanvas();
                            c.getStartPort().connectedConnections.remove(c);
                        }
                        endPort.connectedConnections.clear();
                    }
                }
                connection = new Connection(workspace, startPort, endPort);
            }
            workspace.connectionSet.add(connection);

        }
        /**
         * Return values back to default state in which no connection is being
         * made.
         */

        remove();

    }

    private void remove() {
        workspace.removeChild(this);
        startXProperty().unbind();
        startYProperty().unbind();
        workspace.getScene().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspace.getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
    }
}

package vplcore.workspace;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.graph.util.TypeExtensions;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class PreConnectionModel extends Line {

    private final WorkspaceController workspaceController;
    private final WorkspaceView workspaceView;
    private final PortModel startPort;

    private final EventHandler<MouseEvent> mouseMovedHandler = this::handleMouseMoved;
    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;

    public PreConnectionModel(WorkspaceController workspaceController, PortModel startPort) {
        this.workspaceController = workspaceController;
        this.workspaceView = workspaceController.getView();
        this.startPort = startPort;

        getStyleClass().add("temp-line");

        startXProperty().bind(startPort.centerXProperty);
        startYProperty().bind(startPort.centerYProperty);
        setEndX(startPort.centerXProperty.get());
        setEndY(startPort.centerYProperty.get());

        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // Do NOT use MOUSE_PRESSED or MOUSE_RELEASED otherwise click on port to start connecting and a consecutive click in empty space causes mouseMode to stick in SELECTING
    }

    private void handleMouseMoved(MouseEvent event) {
        setEndX(workspaceView.sceneToLocal(event.getSceneX(), event.getSceneY()).getX());
        setEndY(workspaceView.sceneToLocal(event.getSceneX(), event.getSceneY()).getY());

    }

    private void handleMouseClicked(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onPort = intersectedNode instanceof Port;
        boolean isPrimaryButton = event.getButton() == MouseButton.PRIMARY;
        if (isPrimaryButton && onPort) {
            createConnection((PortModel) intersectedNode);

        } else {
            remove();
        }
        event.consume();
    }

    private void createConnection(PortModel endPort) {

        /**
         * Check if the data type from the sending port is the same or a sub
         * class of the receiving port.
         */
        if (((TypeExtensions.isCastableTo(startPort.dataType, endPort.dataType)
                && workspaceController.typeSensitive && endPort.portType == PortModel.Type.IN)
                || (TypeExtensions.isCastableTo(endPort.dataType, startPort.dataType)
                && workspaceController.typeSensitive && endPort.portType == PortModel.Type.OUT)
                // IN case dataProperty type does not matter
                || (!workspaceController.typeSensitive))
                // Cannot be the same port type; IN > OUT or OUT > IN
                && endPort.portType != startPort.portType
                // Cannot be the same block
                && !endPort.parentBlock.equals(startPort.parentBlock)) {

            /**
             * Make a new connection and remove all the existing connections
             * Where is multi connect?
             */
            if (endPort.portType == PortModel.Type.OUT) {
                if (!startPort.connectedConnections.isEmpty()) {

                    if (!startPort.multiDockAllowed) {
                        for (ConnectionModel c : startPort.connectedConnections) {
                            c.removeFromCanvas();
                        }
                    }
                }
                workspaceController.addConnectionModel(startPort, endPort);

            } else {
                if (!endPort.connectedConnections.isEmpty()) {

                    if (!endPort.multiDockAllowed) {
                        for (ConnectionModel c : endPort.connectedConnections) {
                            c.removeFromCanvas();
                            c.getStartPort().connectedConnections.remove(c);
                        }
                        endPort.connectedConnections.clear();
                    }
                }
                workspaceController.addConnectionModel(startPort, endPort);
            }

        }
        /**
         * Return values back to default state in which no connection is being
         * made.
         */

        remove();

    }

    private void remove() {
        workspaceController.removeChild(this);
        startXProperty().unbind();
        startYProperty().unbind();
        workspaceView.getScene().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspaceView.getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
    }
}

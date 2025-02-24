package vplcore.graph.connection;

import vplcore.graph.port.PortModel;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import vplcore.context.command.RemoveConnectionCommand;
import vplcore.graph.port.PortController;
import vplcore.graph.port.PortType;
import vplcore.graph.port.PortView;
import vplcore.graph.util.TypeExtensions;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class PreConnection extends Line {

    private final WorkspaceController workspaceController;
    private final WorkspaceView workspaceView;
    private final WorkspaceModel workspaceModel;

    private final PortController startPortController;

    public PreConnection(WorkspaceController workspaceController, PortController startPortController) {
        this.workspaceController = workspaceController;
        this.workspaceView = workspaceController.getView();
        this.workspaceModel = workspaceController.getModel();
        this.startPortController = startPortController;

        getStyleClass().add("temp-line");

        startXProperty().bind(startPortController.getView().centerXProperty());
        startYProperty().bind(startPortController.getView().centerYProperty());
        setEndX(startPortController.getView().centerXProperty().get());
        setEndY(startPortController.getView().centerYProperty().get());

        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // Do NOT use MOUSE_PRESSED or MOUSE_RELEASED otherwise click on port to start connecting and a consecutive click in empty space causes mouseMode to stick in SELECTING
    }

    private final EventHandler<MouseEvent> mouseMovedHandler = this::handleMouseMoved;

    private void handleMouseMoved(MouseEvent event) {
        setEndX(workspaceView.sceneToLocal(event.getSceneX(), event.getSceneY()).getX());
        setEndY(workspaceView.sceneToLocal(event.getSceneX(), event.getSceneY()).getY());

    }
    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;

    private void handleMouseClicked(MouseEvent event) {
        System.out.println("PreConnection.handleMouseClicked()");

        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onPort = intersectedNode instanceof PortView;
        boolean isPrimaryButton = event.getButton() == MouseButton.PRIMARY;
        if (isPrimaryButton && onPort) {
            PortView portView = (PortView) intersectedNode;
            String portId = portView.getId();
            PortController portController = workspaceController.getPortController(portId);
            createConnection(portController);

        } else {
            remove();
        }
        event.consume();
    }

    private void createConnection(PortController endPortController) {
        System.out.println("PreConnection.createConnection()");

        PortModel startPortModel = startPortController.getModel();
        PortModel endPortModel = endPortController.getModel();

        /**
         * Check if the data type from the sending port is the same or a sub
         * class of the receiving port.
         */
        if (((TypeExtensions.isCastableTo(startPortModel.dataType, endPortModel.dataType)
                && workspaceController.typeSensitive && endPortModel.portType == PortType.IN)
                || (TypeExtensions.isCastableTo(endPortModel.dataType, startPortModel.dataType)
                && workspaceController.typeSensitive && endPortModel.portType == PortType.OUT)
                // IN case dataProperty type does not matter
                || (!workspaceController.typeSensitive))
                // Cannot be the same port type; IN > OUT or OUT > IN
                && endPortModel.portType != startPortModel.portType
                // Cannot be the same block
                && !endPortModel.parentBlock.equals(startPortModel.parentBlock)) {

            /**
             * Make a new connection and remove all the existing connections
             * Where is multi connect?
             */
            if (endPortModel.portType == PortType.OUT) {
                if (!startPortModel.connections.isEmpty()) {

                    if (!startPortModel.multiDockAllowed) {
                        for (ConnectionModel c : startPortModel.getConnections()) {
                            RemoveConnectionCommand command = new RemoveConnectionCommand(workspaceModel, c);
                            workspaceController.getEditorContext().getActionManager().executeCommand(command);
//                            c.remove();
//                            c.removeFromCanvas();
                        }
                    }
                }
                System.out.println("PreConnection.createConnection() INPUT to OUTPUT");
                workspaceModel.addConnectionModel(endPortModel, startPortModel);

            } else { // endPort is INPUT
                if (!endPortModel.connections.isEmpty()) {

                    if (!endPortModel.multiDockAllowed) {
                        for (ConnectionModel c : endPortModel.getConnections()) {
                            RemoveConnectionCommand command = new RemoveConnectionCommand(workspaceModel, c);
                            workspaceController.getEditorContext().getActionManager().executeCommand(command);
//                            c.remove();
//                            c.removeFromCanvas();
//                            c.getStartPort().connectedConnections.remove(c);
                        }
                        System.out.println("PreConnectionModel endPort.connectedConnections.size()" + endPortModel.connections.size()); // through c.remove() all connection should already be removed so this should print 0
                        endPortModel.connections.clear();
                    }
                }
                System.out.println("PreConnection.createConnection() OUTPUT to INPUT");
                workspaceModel.addConnectionModel(startPortModel, endPortModel);
            }

        }
        /**
         * Return values back to default state in which no connection is being
         * made.
         */

        remove();

    }

    private void remove() {
        workspaceController.removePreConnection(this);
        startXProperty().unbind();
        startYProperty().unbind();
        workspaceView.getScene().removeEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspaceView.getScene().removeEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
    }
}

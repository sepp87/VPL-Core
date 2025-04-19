package btscore.graph.connection;

import btscore.graph.port.PortModel;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import btscore.context.command.CreateConnectionCommand;
import btscore.context.command.RemoveConnectionCommand;
import btscore.graph.port.PortController;
import btscore.graph.port.PortType;
import btscore.graph.port.PortView;
import btscore.graph.util.TypeExtensions;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.workspace.WorkspaceView;

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
        if (ConnectionModel.isEligible(startPortModel, endPortModel)) {
            /**
             * Make a new connection and remove all the existing connections
             * Where is multi connect?
             */
            if (endPortModel.getPortType() == PortType.OUTPUT) {
                System.out.println("PreConnection.createConnection() INPUT to OUTPUT");
                CreateConnectionCommand command = new CreateConnectionCommand(workspaceModel, endPortModel, startPortModel);
                workspaceController.getEditorContext().getActionManager().executeCommand(command);

            } else { // endPort is INPUT
                System.out.println("PreConnection.createConnection() OUTPUT to INPUT");
                CreateConnectionCommand command = new CreateConnectionCommand(workspaceModel, startPortModel, endPortModel);
                workspaceController.getEditorContext().getActionManager().executeCommand(command);
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

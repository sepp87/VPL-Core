package vplcore.workspace.input;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.graph.model.Block;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class PanHandler {

    private final Workspace workspace;

    public PanHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
    }

    private final EventHandler<MouseEvent> mousePressedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.isSecondaryButtonDown() && !workspace.onBlock(event)) {
                workspace.setMouseMode(MouseMode.PANNING);
                preparePan(event);
            }
        }
    };

    private final EventHandler<MouseEvent> mouseDraggedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (workspace.getMouseMode() == MouseMode.PANNING && event.isSecondaryButtonDown()) {
                pan(event);
            }
        }
    };

    private final EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (workspace.getMouseMode() == MouseMode.PANNING && event.getButton() == MouseButton.SECONDARY) {
                workspace.setMouseMode(MouseMode.MOUSE_IDLE);
                boolean wasPanning = workspace.panContext != null;
                if (wasPanning) {
                    removePan();
                }
            }
        }
    };

    private void preparePan(MouseEvent event) {
        workspace.panContext = new DragContext();
        workspace.panContext.setX(event.getSceneX());
        workspace.panContext.setY(event.getSceneY());
        workspace.panContext.setTranslateX(workspace.getTranslateX());
        workspace.panContext.setTranslateY(workspace.getTranslateY());
    }

    private void pan(MouseEvent event) {
        workspace.setTranslateX(workspace.panContext.getTranslateX() + event.getSceneX() - workspace.panContext.getX());
        workspace.setTranslateY(workspace.panContext.getTranslateY() + event.getSceneY() - workspace.panContext.getY());
    }

    private void removePan() {
        workspace.panContext = null;
    }
}

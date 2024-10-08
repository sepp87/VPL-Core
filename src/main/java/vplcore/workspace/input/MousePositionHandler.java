package vplcore.workspace.input;

import vplcore.graph.model.Block;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class MousePositionHandler {

    private final Workspace workspace;
    private Point2D position = new Point2D(0, 0);

    public MousePositionHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);

    }
    private final EventHandler<MouseEvent> mousePressedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            position = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
        }
    };
    private final EventHandler<MouseEvent> mouseMovedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            position = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
        }
    };

    public Point2D getPosition() {
        return new Point2D(position.getX(), position.getY());
    }


}

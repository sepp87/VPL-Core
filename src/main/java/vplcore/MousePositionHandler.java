package vplcore;

import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author joostmeulenkamp
 */
public class MousePositionHandler {

    private final WorkspaceView workspaceView;
    private Point2D position = new Point2D(0, 0);

    public MousePositionHandler(WorkspaceView workspace) {
        this.workspaceView = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEventHandler);
        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseEventHandler);
        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEventHandler);
        workspaceView.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEventHandler);

    }
    private final EventHandler<MouseEvent> mouseEventHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            position = workspaceView.sceneToLocal(event.getSceneX(), event.getSceneY());
            System.out.println("MOVE " + position);

        }
    };

    public Point2D getPosition() {
        return new Point2D(position.getX(), position.getY());
    }

}

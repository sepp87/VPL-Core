package vplcore.workspace.input;

import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Shape3D;
import vplcore.Config;
import vplcore.Util;
import vplcore.workspace.Workspace;
import static vplcore.workspace.Workspace.clamp;

/**
 *
 * @author joostmeulenkamp
 */
public class ZoomHandler {

    private final Workspace workspace;

    public ZoomHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_STARTED, mouseScrollStartedHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL, mouseScrollHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_FINISHED, mouseScrollFinishedHandler);
    }
    
    private final EventHandler<ScrollEvent> mouseScrollStartedHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {
            if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE) {
                workspace.setMouseMode(MouseMode.ZOOMING);
            }
        }
    };
    
    private final EventHandler<ScrollEvent> mouseScrollHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {
            boolean onWindows = Config.get().operatingSystem() == Util.OperatingSystem.WINDOWS;
            if (workspace.getMouseMode() == MouseMode.ZOOMING || onWindows) {
                zoom(event);
            }
        }
    };

    private final EventHandler<ScrollEvent> mouseScrollFinishedHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {
            if (workspace.getMouseMode() == MouseMode.ZOOMING) {
                workspace.setMouseMode(MouseMode.MOUSE_IDLE);
            }
        }
    };

    private void zoom(ScrollEvent event) {
        double delta = Config.get().operatingSystem() == Util.OperatingSystem.WINDOWS ? 1.2 : 1.05;

        double scale = workspace.getScale(); // currently we only use Y, same value is used for X
        double oldScale = scale;

        if (event.getDeltaY() < 0) {
            scale /= delta;
        } else {
            scale *= delta;
        }

        scale = clamp(scale, Workspace.MIN_ZOOM, Workspace.MAX_ZOOM);

        double f = (scale / oldScale) - 1;

        Bounds hack = workspace.localToParent(workspace.zoomPane.getBoundsInParent());

        double dx = (event.getSceneX() - (hack.getWidth() / 2 + hack.getMinX()));
        double dy = (event.getSceneY() - (hack.getHeight() / 2 + hack.getMinY()));

        workspace.setScale(scale);

        // note: pivot value must be untransformed, i. e. without scaling
        workspace.setPivot(f * dx, f * dy);

        event.consume();
    }

}

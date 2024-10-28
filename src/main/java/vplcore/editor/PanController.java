package vplcore.editor;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;

/**
 *
 * @author joostmeulenkamp
 */
public class PanController {

    private final Workspace workspace;
    private final ZoomModel zoomModel;

    private double initialX;
    private double initialY;
    private double initialTranslateX;
    private double initialTranslateY;

    public PanController(Workspace workspace, ZoomModel zoomModel) {
        this.workspace = workspace;
        this.zoomModel = zoomModel;
    }

    public void handleEditorPanStarted(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.isSecondaryButtonDown() && !workspace.onBlock(event)) {
            workspace.setMouseMode(MouseMode.PANNING);
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            initialTranslateX = zoomModel.translateXProperty().get();
            initialTranslateY = zoomModel.translateYProperty().get();
        }
    }

    public void handleEditorPan(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.PANNING && event.isSecondaryButtonDown()) {
            zoomModel.translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
            zoomModel.translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
        }
    }

    public void handleEditorPanStopped(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.PANNING && event.getButton() == MouseButton.SECONDARY) {
            workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        }
    }

}

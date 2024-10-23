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

    public void handleMousePressed(MouseEvent event) {
        System.out.println(event.getSceneX() + "\t" + event.getSceneY() + "\t" + initialTranslateX + "\t" + initialTranslateY + "\t PanController");
        System.out.println(workspace.getMouseMode() + " " + event.isSecondaryButtonDown() + " " + !workspace.onBlock(event));
        if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE || workspace.getMouseMode() == MouseMode.PANNING && event.isSecondaryButtonDown() && !workspace.onBlock(event)) {
            workspace.setMouseMode(MouseMode.PANNING);
            preparePan(event);
        }
    }

    private void preparePan(MouseEvent event) {
        initialX = event.getSceneX();
        initialY = event.getSceneY();
        initialTranslateX = zoomModel.translateXProperty().get();
        initialTranslateY = zoomModel.translateYProperty().get();

        System.out.println(event.getSceneX() + "\t" + event.getSceneY() + "\t" + initialTranslateX + "\t" + initialTranslateY + "\t PanController");
    }

    public void handleMouseDragged(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.PANNING && event.isSecondaryButtonDown()) {
            pan(event);
        }
    }

    private void pan(MouseEvent event) {
        zoomModel.translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
        zoomModel.translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
    }

    public void handleMouseReleased(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.PANNING && event.getButton() == MouseButton.SECONDARY) {
            workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        }
    }

}

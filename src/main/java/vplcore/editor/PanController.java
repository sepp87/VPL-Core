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
        if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.isSecondaryButtonDown() && !workspace.onBlock(event)) {
            workspace.setMouseMode(MouseMode.PANNING);
            preparePan(event);
        }
    }

    private void preparePan(MouseEvent event) {
        initialX = event.getSceneX();
        initialY = event.getSceneY();
        initialTranslateX = workspace.getTranslateX();
        initialTranslateY = workspace.getTranslateY();

//        System.out.println(event.getSceneX() + "\t" + event.getSceneY() + "\t" + initialTranslateX + "\t" + initialTranslateY + "\t PanController");
        System.out.println(workspace.getTranslateX() + "\t" + workspace.getTranslateY() + "\t PanController");
        System.out.println(zoomModel.translateXProperty().get() + "\t" + zoomModel.translateYProperty().get() + "\t PanController");
    }

    public void handleMouseDragged(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.PANNING && event.isSecondaryButtonDown()) {
            pan(event);
        }
    }

    public void handleMouseReleased(MouseEvent event) {
        if (workspace.getMouseMode() == MouseMode.PANNING && event.getButton() == MouseButton.SECONDARY) {
            workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        }
    }

    public void pan(MouseEvent event) {
        workspace.setTranslateX(initialTranslateX + event.getSceneX() - initialX);
        workspace.setTranslateY(initialTranslateY + event.getSceneY() - initialY);
        zoomModel.translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
        zoomModel.translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
    }

}

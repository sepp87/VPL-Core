package vplcore.editor;

import vplcore.workspace.WorkspaceModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.graph.model.Block;
import vplcore.util.NodeHierarchyUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class PanController {

    private final EditorModel editorModel;
    private final WorkspaceModel zoomModel;

    private double initialX;
    private double initialY;
    private double initialTranslateX;
    private double initialTranslateY;

    public PanController(EditorModel editorModel, WorkspaceModel zoomModel) {
        this.editorModel = editorModel;
        this.zoomModel = zoomModel;
    }

    public void processEditorPanStarted(MouseEvent event) {
        if (editorModel.modeProperty().get() == EditorMode.IDLE_MODE && event.isSecondaryButtonDown() && !NodeHierarchyUtils.isPickedNodeOrParentOfType(event, Block.class)) {
            editorModel.modeProperty().set(EditorMode.PAN_MODE);
            initialX = event.getSceneX();
            initialY = event.getSceneY();
            initialTranslateX = zoomModel.translateXProperty().get();
            initialTranslateY = zoomModel.translateYProperty().get();
        }
    }

    public void processEditorPan(MouseEvent event) {
        if (editorModel.modeProperty().get() == EditorMode.PAN_MODE && event.isSecondaryButtonDown()) {
            zoomModel.translateXProperty().set(initialTranslateX + event.getSceneX() - initialX);
            zoomModel.translateYProperty().set(initialTranslateY + event.getSceneY() - initialY);
        }
    }

    public void processEditorPanFinished(MouseEvent event) {
        if (editorModel.modeProperty().get() == EditorMode.PAN_MODE && event.getButton() == MouseButton.SECONDARY) {
            editorModel.modeProperty().set(EditorMode.IDLE_MODE);
        }
    }

}

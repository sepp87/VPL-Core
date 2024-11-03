package vplcore.editor.radialmenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.editor.EditorMode;
import vplcore.editor.EditorModel;
import vplcore.editor.EditorView;
import vplcore.util.NodeHierarchyUtils;
import vplcore.workspace.ActionManager;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController {

    private final ActionManager actionManager;
    private final EditorModel editorModel;
    private final RadialMenuView view;

    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(ActionManager actionManager, EditorModel editorModel, RadialMenuView radialMenuView) {
        this.actionManager = actionManager;
        this.editorModel = editorModel;
        this.view = radialMenuView;

        for (RadialMenuItem item : view.getAllRadialMenuItems()) {
            item.setOnMouseClicked(this::handleRadialMenuItemClicked);
        }

        this.visibilityToggledHandler = this::handleToggleMouseMode;
        view.getRadialMenu().visibleProperty().addListener(visibilityToggledHandler);
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        if (event.getSource() instanceof RadialMenuItem menuItem) {
            actionManager.executeCommand(menuItem.getId());
        }
        hideView();
    }

    private void handleToggleMouseMode(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        if (isVisble) {
            editorModel.modeProperty().set(EditorMode.RADIAL_MENU_MODE);
        } else {
            editorModel.modeProperty().set(EditorMode.IDLE_MODE);
        }
    }

    public void processEditorMouseClicked(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof WorkspaceView;
        boolean onRadialMenu = NodeHierarchyUtils.isNodeOrParentOfType(intersectedNode, RadialMenu.class);
        boolean isSecondaryClick = event.getButton() == MouseButton.SECONDARY && event.isStillSincePress();
        boolean isIdle = editorModel.modeProperty().get() == EditorMode.IDLE_MODE;

        // TODO additional checks needed when 3D viewer is implemented e.g. check against Control.class and Shape3D.class
        if (isSecondaryClick && onEditorOrWorkspace && isIdle) {
            showView(event.getSceneX(), event.getSceneY());
        } else if (onRadialMenu) {
            // keep radial menu shown if it is clicked on
        } else {
            hideView();
        }
    }

    private void showView(double x, double y) {
        view.getRadialMenu().show(x, y);
    }

    private void hideView() {
        view.getRadialMenu().setVisible(false);
    }
}

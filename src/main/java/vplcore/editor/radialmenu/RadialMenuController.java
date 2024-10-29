package vplcore.editor.radialmenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.editor.EditorMode;
import vplcore.editor.EditorModel;
import vplcore.editor.EditorView;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;
import vplcore.util.NodeHierarchyUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController {

    private EditorModel editorModel;
    private Actions actions;

    private final RadialMenuView view;

    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(EditorModel editorModel, RadialMenuView radialMenuView, Actions actions) {
        this.editorModel = editorModel;
        this.actions = actions;
        this.view = radialMenuView;

        for (RadialMenuItem<?> item : view.getAllRadialMenuItems()) {
            item.setOnMouseClicked(this::handleRadialMenuItemClicked);
        }

        this.visibilityToggledHandler = this::handleToggleMouseMode;
        view.getRadialMenu().visibleProperty().addListener(visibilityToggledHandler);
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        @SuppressWarnings("unchecked")
        RadialMenuItem<Actions.ActionType> item = (RadialMenuItem<Actions.ActionType>) event.getSource();
        view.getRadialMenu().setVisible(false);
        actions.perform(item.getAction());
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
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof Workspace;
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

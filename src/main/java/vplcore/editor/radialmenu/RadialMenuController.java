package vplcore.editor.radialmenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.editor.EditorView;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;
import vplcore.editor.radialmenu.RadialMenuItem;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController {

    private Workspace workspace;
    private Actions actions;
    
    private final RadialMenuView view;

    private final EventHandler<MouseEvent> radialMenuItemClickedHandler;
    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(RadialMenuView radialMenuView, Workspace workspace, Actions actions) {
        this.workspace = workspace;
        this.actions = actions;
        this.view = radialMenuView;

        this.radialMenuItemClickedHandler = this::handleRadialMenuItemClicked;
        for (RadialMenuItem<?> item : view.getAllRadialMenuItems()) {
            item.setOnMouseClicked(radialMenuItemClickedHandler);
        }

        this.visibilityToggledHandler = this::handleVisibilityToggled;
        view.getRadialMenu().visibleProperty().addListener(visibilityToggledHandler);
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        @SuppressWarnings("unchecked")
        RadialMenuItem<Actions.ActionType> item = (RadialMenuItem<Actions.ActionType>) event.getSource();
        view.getRadialMenu().setVisible(false);
        actions.perform(item.getAction());
    }

    private void handleVisibilityToggled(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        if (isVisble) {
            workspace.setMouseMode(MouseMode.AWAITING_RADIAL_MENU);
        } else {
            workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        }
    }

    public void handleMouseClicked(MouseEvent event) {
        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof Workspace;
        boolean onRadialMenu = Workspace.checkParents(intersectedNode, RadialMenu.class);
        boolean isSecondaryClick = event.getButton() == MouseButton.SECONDARY && event.isStillSincePress();
        boolean mouseIsIdle = workspace.getMouseMode() == MouseMode.MOUSE_IDLE;

        // TODO additional checks needed when 3D viewer is implemented e.g. check against Control.class and Shape3D.class
        if (isSecondaryClick && onEditorOrWorkspace && mouseIsIdle) {
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

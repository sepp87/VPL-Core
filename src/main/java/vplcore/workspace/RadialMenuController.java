package vplcore.workspace;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.radialmenu.RadialMenuItem;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController {

    private Workspace workspace;
    private final RadialMenuView view;

    private final EventHandler<MouseEvent> radialMenuItemClickedHandler;
    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(RadialMenuView radialMenuView, Workspace workspace) {
        this.workspace = workspace;
        this.view = radialMenuView;

        this.radialMenuItemClickedHandler = this::handleRadialMenuItemClicked;
        for (RadialMenuItem item : view.getAllRadialMenuItems()) {
            item.setOnMouseClicked(radialMenuItemClickedHandler);
        }
        
        this.visibilityToggledHandler = this::handleVisibilityToggled;
        view.getRadialMenu().visibleProperty().addListener(visibilityToggledHandler);
    }

    public void showRadialMenu(double x, double y) {
        view.getRadialMenu().show(x, y);
    }
    
    public void hideRadialMenu() {
        view.getRadialMenu().setVisible(false);
    }

    public void handleVisibilityToggled(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        if (isVisble) {
            workspace.setMouseMode(MouseMode.AWAITING_RADIAL_MENU);
        } else {
            workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        }
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        @SuppressWarnings("unchecked")
        RadialMenuItem<Actions.ActionType> item = (RadialMenuItem<Actions.ActionType>) event.getSource();
        view.getRadialMenu().setVisible(false);
        workspace.actions.perform(item.getAction());
    }

}

package vplcore.editor.radialmenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import vplcore.App;
import vplcore.util.NodeHierarchyUtils;
import vplcore.context.ActionManager;
import vplcore.context.EventRouter;
import vplcore.context.StateManager;
import vplcore.editor.BaseController;
import static vplcore.util.EditorUtils.onFreeSpace;
import static vplcore.util.EventUtils.isRightClick;

/**
 *
 * @author joostmeulenkamp
 */
public class RadialMenuController extends BaseController {

    private final EventRouter eventRouter;
    private final ActionManager actionManager;
    private final StateManager state;
    private final RadialMenuView view;

    private final ChangeListener<Boolean> visibilityToggledHandler;

    public RadialMenuController(String contextId, RadialMenuView radialMenuView) {
        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.actionManager = App.getContext(contextId).getActionManager();
        this.state = App.getContext(contextId).getStateManager();
        this.view = radialMenuView;

        for (RadialMenuItem item : view.getAllRadialMenuItems()) {
            item.setOnMouseClicked(this::handleRadialMenuItemClicked);
        }

        this.visibilityToggledHandler = this::handleToggleMouseMode;
        view.getRadialMenu().visibleProperty().addListener(visibilityToggledHandler);
        eventRouter.addEventListener(MouseEvent.MOUSE_CLICKED, this::showRadialMenu);
        eventRouter.addEventListener(MouseEvent.MOUSE_CLICKED, this::hideRadialMenu);
    }

    private void showRadialMenu(MouseEvent event) {
        boolean onFreeSpace = onFreeSpace(event);
        boolean isIdle = state.isIdle();
        if (isRightClick(event) && onFreeSpace && (isIdle || view.getRadialMenu().isVisible())) {
            showView(event.getSceneX(), event.getSceneY());
        }
    }

    private void hideRadialMenu(MouseEvent event) {
        if (!NodeHierarchyUtils.isPickedNodeOrParentOfType(event, RadialMenu.class)) {
            hideView();
        }
    }

    private void handleRadialMenuItemClicked(MouseEvent event) {
        if (event.getSource() instanceof RadialMenuItem menuItem) {
            actionManager.executeCommand(menuItem.getId());
        }
        hideView();
    }

    private void handleToggleMouseMode(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        if (isVisble) {
            state.setAwaitingRadialMenu();
        } else {
            state.setIdle();
        }
    }

    private void showView(double x, double y) {
        view.getRadialMenu().show(x, y);
    }

    private void hideView() {
        view.getRadialMenu().setVisible(false);
    }
}

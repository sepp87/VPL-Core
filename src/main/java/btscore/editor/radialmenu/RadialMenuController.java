package btscore.editor.radialmenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.MouseEvent;
import btscore.App;
import btscore.util.NodeHierarchyUtils;
import btscore.context.ActionManager;
import btscore.context.EventRouter;
import btscore.context.StateManager;
import btscore.editor.BaseController;
import static btscore.util.EditorUtils.onFreeSpace;
import static btscore.util.EventUtils.isRightClick;

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
        eventRouter.addEventListener(MouseEvent.MOUSE_CLICKED, this::toggleRadialMenu);
    }

    private void toggleRadialMenu(MouseEvent event) {
        if (isRightClick(event) && onFreeSpace(event) && (state.isIdle() || view.getRadialMenu().isVisible())) {
            showView(event.getSceneX(), event.getSceneY());
            
        } else if (!NodeHierarchyUtils.isPickedNodeOrParentOfType(event, RadialMenu.class)) {
            // hide radial menu if any kind of click was anywhere else than on the menu
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

package vplcore.editor;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import vplcore.App;
import vplcore.context.ActionManager;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController extends BaseController {

    private final ActionManager actionManager;
    private final MenuBarView view;

    private final EventHandler<ActionEvent> menuBarItemClickedHandler;

    public MenuBarController(String contextId, MenuBarView menuBarView) {
        super(contextId);
        this.actionManager = App.getContext(contextId).getActionManager();
        this.view = menuBarView;

        menuBarItemClickedHandler = this::handleMenuBarItemClicked;
        for (MenuItem item : view.getAllMenuItems()) {
            item.setOnAction(menuBarItemClickedHandler);
        }

        view.getGroupMenuItem().getParentMenu().showingProperty().addListener(groupMenuItemVisibilityListener);
    }

    private final ChangeListener<Boolean> groupMenuItemVisibilityListener = this::onGroupMenuItemVisibilityChanged;

    private void onGroupMenuItemVisibilityChanged(Object b, Boolean o, Boolean n) {
        boolean isGroupable = this.getEditorContext().getActionManager().getWorkspaceController().areSelectedBlocksGroupable();
        view.getGroupMenuItem().disableProperty().set(!isGroupable);
        System.out.println("onGroupMenuItemVisibilityChanged to " + n + " isGroupable " + isGroupable);

    }

    private void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            actionManager.executeCommand(menuItem.getId());
        }
    }

}

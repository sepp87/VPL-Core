package vplcore.editor;

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
    }

    public void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            actionManager.executeCommand(menuItem.getId());
        }
    }

}

package vplcore.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import vplcore.workspace.ActionManager;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private ActionManager actionManager;
    private final MenuBarView view;

    private final EventHandler<ActionEvent> menuBarItemClickedHandler;

    public MenuBarController(ActionManager actionManager, MenuBarView menuBarView) {
        this.actionManager = actionManager;
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

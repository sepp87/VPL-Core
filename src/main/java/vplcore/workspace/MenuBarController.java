package vplcore.workspace;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import vplcore.workspace.MenuBarView1.MenuBarItem;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private Workspace workspace;
    private final MenuBarView1 view;

    private final EventHandler<ActionEvent> menuBarItemClickedHandler;

    public MenuBarController(MenuBarView1 menuBarView, Workspace workspace) {
        this.workspace = workspace;
        this.view = menuBarView;

        this.menuBarItemClickedHandler = this::handleMenuBarItemClicked;
        for (MenuBarItem item : view.getAllMenuBarItems()) {
            item.setOnAction(menuBarItemClickedHandler);
        }
    }

    public void handleMenuBarItemClicked(ActionEvent event) {
        @SuppressWarnings("unchecked")
        MenuBarItem item = (MenuBarItem) event.getSource();
        workspace.actions.perform(item.getAction());
    }

}

package vplcore.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import vplcore.editor.MenuBarView.MenuBarItem;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private Workspace workspace;
    private final MenuBarView view;

    private final EventHandler<ActionEvent> menuBarItemClickedHandler;

    public MenuBarController(MenuBarView menuBarView, Workspace workspace) {
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

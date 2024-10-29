package vplcore.editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import vplcore.editor.MenuBarView.MenuBarItem;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarController {

    private Actions actions;
    
    private final MenuBarView view;

    private final EventHandler<ActionEvent> menuBarItemClickedHandler;

    public MenuBarController(MenuBarView menuBarView, Actions actions) {
        this.actions = actions;
        
        this.view = menuBarView;

        menuBarItemClickedHandler = this::handleMenuBarItemClicked;
        for (MenuBarItem item : view.getAllMenuBarItems()) {
            item.setOnAction(menuBarItemClickedHandler);
        }
    }

    public void handleMenuBarItemClicked(ActionEvent event) {
        @SuppressWarnings("unchecked")
        MenuBarItem item = (MenuBarItem) event.getSource();
        actions.perform(item.getAction());
    }

}

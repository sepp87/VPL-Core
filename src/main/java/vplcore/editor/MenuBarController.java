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
    
    public MenuBarController(String contextId, MenuBarView menuBarView) {
        super(contextId);
        this.actionManager = App.getContext(contextId).getActionManager();
        this.view = menuBarView;
        
        for (MenuItem item : view.getAllMenuItems()) {
            item.setOnAction(menuBarItemClickedHandler);
        }
        
        view.getUndoMenuItem().setOnAction((e) -> undo());
        view.getRedoMenuItem().setOnAction((e) -> redo());
        
        view.getGroupMenuItem().getParentMenu().showingProperty().addListener(groupMenuItemVisibilityListener);
    }
    
    private void undo() {
        actionManager.undo();
    }
    
    private void redo() {
        actionManager.redo();
    }
    
    private final ChangeListener<Boolean> groupMenuItemVisibilityListener = this::onGroupMenuItemVisibilityChanged;
    
    private void onGroupMenuItemVisibilityChanged(Object b, Boolean o, Boolean n) {
        boolean isGroupable = this.getEditorContext().getActionManager().getWorkspaceController().areSelectedBlocksGroupable();
        view.getGroupMenuItem().disableProperty().set(!isGroupable);
        System.out.println("onGroupMenuItemVisibilityChanged to " + n + " isGroupable " + isGroupable);
        
    }
    private final EventHandler<ActionEvent> menuBarItemClickedHandler = this::handleMenuBarItemClicked;
    
    private void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            actionManager.executeCommand(menuItem.getId());
        }
    }
    
}

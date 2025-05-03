package btscore.editor;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import btscore.App;
import btscore.Config;
import btscore.editor.context.ActionManager;

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

        view.getFileMenu().showingProperty().addListener(fileMenuShownListener);
        view.getEditMenu().showingProperty().addListener(editMenuShownListener);
        view.getUndoMenuItem().setOnAction((e) -> undo());
        view.getRedoMenuItem().setOnAction((e) -> redo());

        for (MenuItem styleItem : view.getStyleMenuItems()) {
            styleItem.setOnAction((e) -> Config.setStylesheet(view.getScene(), styleItem.getText()));
        }
    }

    private void undo() {
        actionManager.undo();
    }

    private void redo() {
        actionManager.redo();
    }

    private final ChangeListener<Boolean> fileMenuShownListener = this::onFileMenuShown;

    private void onFileMenuShown(Object b, Boolean o, Boolean n) {
        view.getSaveMenuItem().setDisable(!actionManager.getWorkspaceModel().savableProperty().get());
    }

    private final ChangeListener<Boolean> editMenuShownListener = this::onEditMenuShown;

    private void onEditMenuShown(Object b, Boolean o, Boolean n) {
        view.getUndoMenuItem().setDisable(!actionManager.hasUndoableCommands());
        view.getRedoMenuItem().setDisable(!actionManager.hasRedoableCommands());

        boolean isGroupable = actionManager.getWorkspaceController().areSelectedBlocksGroupable();
        view.getGroupMenuItem().disableProperty().set(!isGroupable);
    }

    private final EventHandler<ActionEvent> menuBarItemClickedHandler = this::handleMenuBarItemClicked;

    private void handleMenuBarItemClicked(ActionEvent event) {
        if (event.getSource() instanceof MenuItem menuItem) {
            actionManager.executeCommand(menuItem.getId());
        }
    }

}

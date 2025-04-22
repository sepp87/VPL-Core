package btscore.editor;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import btscore.App;
import btscore.util.EventUtils;
import btscore.editor.context.ActionManager;
import btscore.editor.context.EventRouter;
import btscore.editor.context.Command;
import btscore.editor.context.EditorContext;
import btscore.editor.commands.CopyBlocksCommand;
import btscore.editor.commands.RemoveSelectedBlocksCommand;
import btscore.editor.commands.GroupBlocksCommand;
import btscore.editor.commands.NewFileCommand;
import btscore.editor.commands.OpenFileCommand;
import btscore.editor.commands.PasteBlocksCommand;
import btscore.editor.commands.SaveAsFileCommand;
import btscore.editor.commands.SaveFileCommand;
import btscore.editor.commands.SelectAllBlocksCommand;
import btscore.editor.commands.ZoomInCommand;
import btscore.editor.commands.ZoomOutCommand;
import btscore.editor.commands.ZoomToFitCommand;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController {


    public static void handleShortcutTriggered(KeyEvent event) {
        ActionManager actionManager = App.getCurrentContext().getActionManager();
        
        if (App.LOG_METHOD_CALLS) {
            System.out.println("KeyboardController.handleShortcutTriggered()");
        }
        Command command = null;
        boolean isModifierDown = EventUtils.isModifierDown(event);
        switch (event.getCode()) {
            case BACK_SPACE:
            case DELETE:
                command = new RemoveSelectedBlocksCommand(actionManager.getWorkspaceController());
                break;
            case C:
                if (isModifierDown) {
                    command = new CopyBlocksCommand(actionManager.getWorkspaceController());
                }
                break;
            case V:
                if (isModifierDown) {
                    command = new PasteBlocksCommand(actionManager.getWorkspaceController(), actionManager.getWorkspaceModel());
                }
                break;
            case G:
                if (isModifierDown) {
                    WorkspaceController workspaceController = actionManager.getWorkspaceController();
                    boolean isGroupable = workspaceController.areSelectedBlocksGroupable();
                    if (isGroupable) {
                        command = new GroupBlocksCommand(actionManager.getWorkspaceController(), actionManager.getWorkspaceModel());
                    }
                }
                break;
            case N:
                if (isModifierDown) {
                    command = new NewFileCommand(actionManager.getWorkspaceModel());
                }
                break;
            case S:
                if (isModifierDown) {
                    if (event.isShiftDown()) {
                        command = new SaveAsFileCommand(actionManager.getWorkspaceModel());

                    } else if (actionManager.getWorkspaceModel().savableProperty().get()) {
                        command = new SaveFileCommand(actionManager.getWorkspaceModel());
                    }
                }
                break;
            case O:
                if (isModifierDown) {
                    command = new OpenFileCommand(actionManager.getWorkspaceModel());
                }
                break;
            case A:
                if (isModifierDown) {
                    command = new SelectAllBlocksCommand(actionManager.getWorkspaceController());
                }
                break;
            case PLUS:
                if (isModifierDown) {
                    command = new ZoomInCommand(actionManager.getWorkspaceController());
                }
                break;
            case MINUS:
                if (isModifierDown) {
                    command = new ZoomOutCommand(actionManager.getWorkspaceController());
                }
                break;
            case SPACE:
                command = new ZoomToFitCommand(actionManager.getWorkspaceController());
                break;
            case Z:
                if (isModifierDown) {
                    actionManager.undo();
                }
                break;
            case Y:
                if (isModifierDown) {
                    actionManager.redo();
                }
                break;

        }
        if (command != null) {
            actionManager.executeCommand(command);
        }
    }
}

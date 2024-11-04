package vplcore.editor;

import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import vplcore.util.EventUtils;
import vplcore.workspace.ActionManager;
import vplcore.workspace.Command;
import vplcore.workspace.command.CopyBlocksCommand;
import vplcore.workspace.command.DeleteSelectedBlocksCommand;
import vplcore.workspace.command.GroupBlocksCommand;
import vplcore.workspace.command.NewFileCommand;
import vplcore.workspace.command.OpenFileCommand;
import vplcore.workspace.command.PasteBlocksCommand;
import vplcore.workspace.command.SaveFileCommand;
import vplcore.workspace.command.SelectAllBlocksCommand;
import vplcore.workspace.command.ZoomInCommand;
import vplcore.workspace.command.ZoomOutCommand;
import vplcore.workspace.command.ZoomToFitCommand;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController {

    private final ActionManager actionManager;

    public KeyboardController(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public void processEditorShortcutAction(KeyEvent event) {
        Command command = null;
        boolean isModifierDown = EventUtils.isModifierDown(event);
        switch (event.getCode()) {
            case BACK_SPACE:
            case DELETE:
                command = new DeleteSelectedBlocksCommand(actionManager.getWorkspaceController());
                break;
            case C:
                if (isModifierDown) {
                    command = new CopyBlocksCommand(actionManager.getWorkspaceController());
                }
                break;
            case V:
                if (isModifierDown) {
                    command = new PasteBlocksCommand(actionManager.getWorkspaceController());
                }
                break;
            case G:
                if (isModifierDown) {
                    command = new GroupBlocksCommand(actionManager.getWorkspaceController());
                }
                break;
            case N:
                if (isModifierDown) {
                    command = new NewFileCommand(actionManager.getWorkspaceController());
                }
                break;
            case S:
                if (isModifierDown) {
                    command = new SaveFileCommand(actionManager.getWorkspaceController());
                }
                break;
            case O:
                if (isModifierDown) {
                    command = new OpenFileCommand(actionManager.getWorkspaceController());
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
                    // TODO undo
                }
                break;
            case Y:
                if (isModifierDown) {
                    // TODO redo
                }
                break;

        }
        if (command != null) {
            actionManager.executeCommand(command);
        }
    }
}

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
import btscore.context.ActionManager;
import btscore.context.EventRouter;
import btscore.context.Command;
import btscore.context.command.CopyBlocksCommand;
import btscore.context.command.RemoveSelectedBlocksCommand;
import btscore.context.command.GroupBlocksCommand;
import btscore.context.command.NewFileCommand;
import btscore.context.command.OpenFileCommand;
import btscore.context.command.PasteBlocksCommand;
import btscore.context.command.SaveAsFileCommand;
import btscore.context.command.SaveFileCommand;
import btscore.context.command.SelectAllBlocksCommand;
import btscore.context.command.ZoomInCommand;
import btscore.context.command.ZoomOutCommand;
import btscore.context.command.ZoomToFitCommand;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardController extends BaseController {

    private final EventRouter eventRouter;
    private final ActionManager actionManager;

    public KeyboardController(String contextId) {
        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.actionManager = App.getContext(contextId).getActionManager();

        eventRouter.addEventListener(KeyEvent.KEY_PRESSED, this::handleShortcutTriggered);
    }

    public void handleShortcutTriggered(KeyEvent event) {
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

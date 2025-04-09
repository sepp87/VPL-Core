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
import vplcore.App;
import vplcore.util.EventUtils;
import vplcore.context.ActionManager;
import vplcore.context.EventRouter;
import vplcore.context.Command;
import vplcore.context.command.CopyBlocksCommand;
import vplcore.context.command.RemoveSelectedBlocksCommand;
import vplcore.context.command.GroupBlocksCommand;
import vplcore.context.command.NewFileCommand;
import vplcore.context.command.OpenFileCommand;
import vplcore.context.command.PasteBlocksCommand;
import vplcore.context.command.SaveAsFileCommand;
import vplcore.context.command.SaveFileCommand;
import vplcore.context.command.SelectAllBlocksCommand;
import vplcore.context.command.ZoomInCommand;
import vplcore.context.command.ZoomOutCommand;
import vplcore.context.command.ZoomToFitCommand;
import vplcore.workspace.WorkspaceController;

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
        if (App.FUTURE_TESTS) {
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
                    command = new NewFileCommand(actionManager.getWorkspaceController());
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

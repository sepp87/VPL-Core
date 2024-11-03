package vplcore.workspace;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import vplcore.workspace.command.AlignBottomCommand;
import vplcore.workspace.command.AlignHorizontallyCommand;
import vplcore.workspace.command.AlignLeftCommand;
import vplcore.workspace.command.AlignRightCommand;
import vplcore.workspace.command.AlignTopCommand;
import vplcore.workspace.command.AlignVerticallyCommand;
import vplcore.workspace.command.CopyBlocksCommand;
import vplcore.workspace.command.DeleteSelectedBlocksCommand;
import vplcore.workspace.command.GroupBlocksCommand;
import vplcore.workspace.command.NewFileCommand;
import vplcore.workspace.command.OpenFileCommand;
import vplcore.workspace.command.PasteBlocksCommand;
import vplcore.workspace.command.SaveFileCommand;
import vplcore.workspace.command.ZoomInCommand;
import vplcore.workspace.command.ZoomOutCommand;
import vplcore.workspace.command.ZoomToFitCommand;

/**
 *
 * @author Joost
 */
public class ActionManager {

    private final WorkspaceController workspaceController;

    private final Stack<Undoable> undoStack = new Stack<>();
    private final Stack<Undoable> redoStack = new Stack<>();
    private final Map<String, Command> commandRegistry = new HashMap<>();

    public ActionManager(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
        initializeCommands();
    }

    private void initializeCommands() {
        commandRegistry.put("NEW_FILE", new NewFileCommand(workspaceController));
        commandRegistry.put("OPEN_FILE", new OpenFileCommand(workspaceController));
        commandRegistry.put("SAVE_FILE", new SaveFileCommand(workspaceController));
        commandRegistry.put("COPY_BLOCKS", new CopyBlocksCommand(workspaceController));
        commandRegistry.put("PASTE_BLOCKS", new PasteBlocksCommand(workspaceController));
        commandRegistry.put("DELETE_SELECTED_BLOCKS", new DeleteSelectedBlocksCommand(workspaceController));
        commandRegistry.put("GROUP_BLOCKS", new GroupBlocksCommand(workspaceController));
        commandRegistry.put("ALIGN_LEFT", new AlignLeftCommand(workspaceController));
        commandRegistry.put("ALIGN_VERTICALLY", new AlignVerticallyCommand(workspaceController));
        commandRegistry.put("ALIGN_RIGHT", new AlignRightCommand(workspaceController));
        commandRegistry.put("ALIGN_TOP", new AlignTopCommand(workspaceController));
        commandRegistry.put("ALIGN_HORIZONTALLY", new AlignHorizontallyCommand(workspaceController));
        commandRegistry.put("ALIGN_BOTTOM", new AlignBottomCommand(workspaceController));
        commandRegistry.put("ZOOM_TO_FIT", new ZoomToFitCommand(workspaceController));
        commandRegistry.put("ZOOM_IN", new ZoomInCommand(workspaceController));
        commandRegistry.put("ZOOM_OUT", new ZoomOutCommand(workspaceController));
    }

    public WorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public void executeCommand(String id) {
        Command command = commandRegistry.get(id);
        if (command != null) {
            executeCommand(command);
        }
    }

    public void executeCommand(Command command) {
        command.execute();
        if (command instanceof Undoable undoable) {
            undoStack.push(undoable);
        }
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Undoable command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Undoable command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}

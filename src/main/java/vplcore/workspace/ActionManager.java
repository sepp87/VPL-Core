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

    private final Workspace workspace;

    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();
    private final Map<String, Command> commandRegistry = new HashMap<>();

    public ActionManager(Workspace workspace) {
        this.workspace = workspace;
        initializeCommands();
    }

    private void initializeCommands() {
        commandRegistry.put("NEW_FILE", new NewFileCommand(workspace));
        commandRegistry.put("OPEN_FILE", new OpenFileCommand(workspace));
        commandRegistry.put("SAVE_FILE", new SaveFileCommand(workspace));
        commandRegistry.put("COPY_BLOCKS", new CopyBlocksCommand(workspace));
        commandRegistry.put("PASTE_BLOCKS", new PasteBlocksCommand(workspace));
        commandRegistry.put("DELETE_SELECTED_BLOCKS", new DeleteSelectedBlocksCommand(workspace));
        commandRegistry.put("GROUP_BLOCKS", new GroupBlocksCommand(workspace));
        commandRegistry.put("ALIGN_LEFT", new AlignLeftCommand(workspace));
        commandRegistry.put("ALIGN_VERTICALLY", new AlignVerticallyCommand(workspace));
        commandRegistry.put("ALIGN_RIGHT", new AlignRightCommand(workspace));
        commandRegistry.put("ALIGN_TOP", new AlignTopCommand(workspace));
        commandRegistry.put("ALIGN_HORIZONTALLY", new AlignHorizontallyCommand(workspace));
        commandRegistry.put("ALIGN_BOTTOM", new AlignBottomCommand(workspace));
        commandRegistry.put("ZOOM_TO_FIT", new ZoomToFitCommand(workspace));
        commandRegistry.put("ZOOM_IN", new ZoomInCommand(workspace));
        commandRegistry.put("ZOOM_OUT", new ZoomOutCommand(workspace));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void executeCommand(String id) {
        Command command = commandRegistry.get(id);
        if (command != null) {
            executeCommand(command);
        }
    }

    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            Command command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }
}

package vplcore.context;

import java.util.ArrayDeque;
import java.util.Deque;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public class ActionManager {

    private final WorkspaceModel workspaceModel;
    private final WorkspaceController workspaceController;
    
    private final CommandFactory commandFactory;

    private final Deque<UndoableCommand> undoStack = new ArrayDeque<>();
    private final Deque<UndoableCommand> redoStack = new ArrayDeque<>();

    public ActionManager(WorkspaceModel workspaceModel, WorkspaceController workspaceController) {
        this.workspaceModel = workspaceModel;
        this.workspaceController = workspaceController;
        this.commandFactory = new CommandFactory(workspaceModel, workspaceController);
    }

    public WorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public WorkspaceModel getWorkspaceModel() {
        return workspaceModel;
    }

    public void executeCommand(String id) {
        Command command = commandFactory.createCommand(id);
        if (command != null) {
            executeCommand(command);
        } else {
            System.out.println("Command not found: " + id);
        }
    }

    public void executeCommand(Command command) {
        boolean isSuccessful = command.execute();
        if (isSuccessful) {
            if (command instanceof UndoableCommand undoable) {
                undoStack.push(undoable);
                redoStack.clear();
            } else if (command instanceof ResetHistoryCommand) {
                resetHistory();
            }
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            UndoableCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            UndoableCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    public void resetHistory() {
        undoStack.clear();
        redoStack.clear();
    }

    public boolean hasUndoableCommands() {
        return !undoStack.isEmpty();
    }

    public boolean hasRedoableCommands() {
        return !redoStack.isEmpty();
    }
}

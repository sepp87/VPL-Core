package vplcore.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
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
    
    private final Deque<Command> executedCommands = new ArrayDeque<>();
    private final Deque<Command> awaitingCommands = new ArrayDeque<>();
    
    public void executeCommand(Command command) {
        boolean isSuccessful = command.execute();
        if (isSuccessful) {
            executedCommands.push(command);
            awaitingCommands.clear();
            setWorkspaceSavable();
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
            
            pushAwaitingCommands();
            setWorkspaceSavable();
        }
    }

    // block, move, move, 
    // block, save, block
    private void pushAwaitingCommands() {
        Command executed = executedCommands.pop();
        awaitingCommands.push(executed);
        if (!(executed instanceof UndoableCommand)) {
            pushAwaitingCommands();
        }
    }
    
    public void redo() {
        if (!redoStack.isEmpty()) {
            UndoableCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
            
            pushExecutedCommands();
            setWorkspaceSavable();
        }
    }
    
    private void pushExecutedCommands() {
        Command awaiting = awaitingCommands.pop();
        executedCommands.push(awaiting);
        if (!(awaitingCommands.peek() instanceof UndoableCommand)) {
            pushExecutedCommands();
        }
    }
    
    private void setWorkspaceSavable() {
        Iterator<Command> iterator = executedCommands.iterator();
        while (iterator.hasNext()) {
            Command command = iterator.next();
            if (command instanceof UndoableCommand) {
                workspaceModel.savableProperty().set(true);
                break;
            }
            if (command instanceof DisableSaveCommand) {
                workspaceModel.savableProperty().set(false);
                break;
            }
        }
    }
    
    public void resetHistory() {
        executedCommands.clear();
        awaitingCommands.clear();
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

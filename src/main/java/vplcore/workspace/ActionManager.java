package vplcore.workspace;

import java.util.Stack;

/**
 *
 * @author Joost
 */

public class ActionManager {

    private final Workspace workspace;
    
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    public ActionManager(Workspace workspace) {
        this.workspace = workspace;
    }
    
    public Workspace getWorkspace(){
        return workspace;
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

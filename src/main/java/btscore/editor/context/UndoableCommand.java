package btscore.editor.context;

import btscore.editor.context.Command;

/**
 *
 * @author Joost
 */
public interface UndoableCommand extends Command {
    
    public void undo();
}

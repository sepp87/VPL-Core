package btscore.context;

import btscore.context.Command;

/**
 *
 * @author Joost
 */
public interface UndoableCommand extends Command {
    
    public void undo();
}

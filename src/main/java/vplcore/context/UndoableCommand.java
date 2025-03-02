package vplcore.context;

import vplcore.context.Command;

/**
 *
 * @author Joost
 */
public interface UndoableCommand extends Command {
    
    public void undo();
}

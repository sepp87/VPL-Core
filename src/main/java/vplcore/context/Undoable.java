package vplcore.context;

import vplcore.context.Command;

/**
 *
 * @author Joost
 */
public interface Undoable extends Command {
    
    public void undo();
}

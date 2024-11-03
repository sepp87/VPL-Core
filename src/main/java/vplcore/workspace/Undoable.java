package vplcore.workspace;

/**
 *
 * @author Joost
 */
public interface Undoable extends Command {
    
    public void undo();
}

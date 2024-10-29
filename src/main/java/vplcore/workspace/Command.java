package vplcore.workspace;

/**
 *
 * @author Joost
 */
public interface Command {

    void execute();

    void undo();
}

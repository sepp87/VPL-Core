package jo.vpl.core;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyConnection {

    public Hub oldHub;
    public Hub newHub;
    
    public CopyConnection(Hub oldHub, Hub newHub){
        this.oldHub = oldHub;
        this.newHub = newHub;
    }
}

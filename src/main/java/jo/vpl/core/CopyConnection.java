package jo.vpl.core;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyConnection {

    public Block oldHub;
    public Block newHub;
    
    public CopyConnection(Block oldHub, Block newHub){
        this.oldHub = oldHub;
        this.newHub = newHub;
    }
}

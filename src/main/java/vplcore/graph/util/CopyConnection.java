package vplcore.graph.util;

import vplcore.graph.model.Block;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopyConnection {

    public Block oldBlock;
    public Block newBlock;
    
    public CopyConnection(Block oldBlock, Block newBlock){
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }
}

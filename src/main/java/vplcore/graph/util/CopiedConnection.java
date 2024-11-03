package vplcore.graph.util;

import vplcore.graph.model.Block;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopiedConnection {

    public Block oldBlock;
    public Block newBlock;
    
    public CopiedConnection(Block oldBlock, Block newBlock){
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }
}

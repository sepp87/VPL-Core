package btscore.graph.connection;

import btscore.graph.block.BlockModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopiedConnection {

    public BlockModel oldBlock;
    public BlockModel newBlock;
    
    public CopiedConnection(BlockModel oldBlock, BlockModel newBlock){
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }
}

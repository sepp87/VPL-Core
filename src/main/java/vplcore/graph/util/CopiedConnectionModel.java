package vplcore.graph.util;

import vplcore.workspace.BlockModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class CopiedConnectionModel {

    public BlockModel oldBlock;
    public BlockModel newBlock;
    
    public CopiedConnectionModel(BlockModel oldBlock, BlockModel newBlock){
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
    }
}

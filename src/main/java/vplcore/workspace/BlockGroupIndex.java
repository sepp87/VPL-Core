package vplcore.workspace;

import java.util.HashMap;
import java.util.Map;
import vplcore.graph.block.BlockModel;
import vplcore.graph.group.BlockGroupModel;

/**
 * Registers which block belongs to which group. To provide easy access to a
 * block's group and ensure blocks are only grouped once.
 *
 * @author Joost
 */
public class BlockGroupIndex {

    private final Map<BlockModel, BlockGroupModel> blockGroupMap = new HashMap<>();

    public void register(BlockModel block, BlockGroupModel group) {
        blockGroupMap.put(block, group);
    }

    public void unregister(BlockModel block) {
        blockGroupMap.remove(block);
    }

    public BlockGroupModel getBlockGroup(BlockModel block) {
        return blockGroupMap.get(block);
    }

//    public Map<BlockModel, BlockGroupModel> getAllMappings() {
//        return Collections.unmodifiableMap(blockGroupMap);
//    }
}

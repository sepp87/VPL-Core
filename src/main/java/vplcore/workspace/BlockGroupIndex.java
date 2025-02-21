package vplcore.workspace;

import java.util.HashMap;
import java.util.Map;
import vplcore.graph.block.BlockModel;
import vplcore.graph.group.BlockGroupModel;

/**
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

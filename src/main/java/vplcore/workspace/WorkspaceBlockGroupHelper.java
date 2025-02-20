package vplcore.workspace;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import vplcore.graph.block.BlockModel;
import vplcore.graph.group.BlockGroupModel;

/**
 *
 * @author JoostMeulenkamp
 */
public class WorkspaceBlockGroupHelper {

    private final WorkspaceModel workspaceModel;
    private final ObservableMap<BlockModel, BlockGroupModel> blockGroupIndex = FXCollections.observableHashMap();

    public WorkspaceBlockGroupHelper(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
    }

    public void createBlockGroup(List<BlockModel> blockModels) {
        for (BlockModel blockModel : blockModels) {
            if (isGrouped(blockModel)) {
                return; // handle block cannot be created
            }
        }

        BlockGroupModel blockGroupModel = new BlockGroupModel(workspaceModel);
        blockGroupModel.setBlocks(blockModels);

        for (BlockModel blockModel : blockModels) {
            blockGroupIndex.put(blockModel, blockGroupModel);
        }
    }

    private boolean isGrouped(BlockModel blockModel) {
        return blockGroupIndex.containsKey(blockModel);
    }

}

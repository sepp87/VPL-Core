package vplcore.context.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import vplcore.App;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.group.BlockGroupModel;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;
import vplcore.context.UndoableCommand;

/**
 *
 * @author Joost
 */
public class RemoveSelectedBlocksCommand implements UndoableCommand {

    private final WorkspaceController workspaceController;
    private final Collection<BlockController> blocks;
    private final Collection<ConnectionModel> connections = new HashSet<>();
    private final Map<BlockGroupModel, List<BlockModel>> blockGroups = new TreeMap<>();

    public RemoveSelectedBlocksCommand(WorkspaceController workspaceController) {
        this.workspaceController = workspaceController;
        this.blocks = workspaceController.getSelectedBlockControllers();
    }

    @Override
    public boolean execute() {
        if (App.FUTURE_TESTS) {
            System.out.println("RemoveSelectedBlocksCommand.execute()");
        }
        workspaceController.deselectAllBlocks();

        // first retrieve all groups, because they could be removed if the number of grouped blocks is reduced below two
        WorkspaceModel workspaceModel = workspaceController.getModel();
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            BlockGroupModel group = workspaceModel.getBlockGroup(blockModel);
            if (group == null) {
                continue;
            }
            if (blockGroups.containsKey(group)) {
                List<BlockModel> blocksInGroup = blockGroups.get(group);
                blocksInGroup.add(blockModel);
            } else {
                List<BlockModel> blocksInGroup = new ArrayList<>();
                blocksInGroup.add(blockModel);
                blockGroups.put(group, blocksInGroup);
            }
        }

        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            // remove connections before, otherwise the connections are already removed from the block
            Collection<ConnectionModel> removedConnections = workspaceModel.removeConnectionModels(blockModel);
            connections.addAll(removedConnections);
            workspaceModel.removeBlockModel(blockModel);
            workspaceModel.removeBlockFromGroup(blockModel); // can be null and could also be removed
        }
        return true;

    }

    @Override
    public void undo() {
        workspaceController.deselectAllBlocks();

        // first revive all blocks, because connections could have been between blocks that have both been removed and groups could have been removed, since the number of grouped blocks was reduced below two
        WorkspaceModel workspaceModel = workspaceController.getModel();
        for (BlockController blockController : blocks) {
            BlockModel blockModel = blockController.getModel();
            blockModel.revive();
            workspaceModel.addBlockModel(blockModel);
            workspaceController.selectBlock(blockModel);
        }

        // revive connections
        for (ConnectionModel connection : connections) {
            connection.revive();
            workspaceModel.addConnectionModel(connection);
        }

        // regroup blocks, if needed revive the group
        for (BlockGroupModel group : blockGroups.keySet()) {
            if (group.isRemoved()) {
                group.revive();
            }
            group.setBlocks(blockGroups.get(group));
            workspaceModel.addBlockGroupModel(group);
        }

    }
}

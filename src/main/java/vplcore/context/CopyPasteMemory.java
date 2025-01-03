package vplcore.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.graph.model.Block;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.graph.util.CopiedConnection;
import vplcore.graph.util.CopiedConnectionModel;
import vplcore.workspace.BlockController;
import vplcore.workspace.BlockModel;
import vplcore.workspace.BlockView;
import vplcore.workspace.ConnectionModel;
import vplcore.workspace.PortModel;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class CopyPasteMemory {

    private static CopyResult clipboard;

    public static final boolean containsItems() {
        return clipboard != null;
    }

    public static final CopyResult getCopyResult() {
        if (clipboard == null) {
            return null;
        }

        if (vplcore.App.BLOCK_MVC) {
            CopyResult copy = copyBlockModels(clipboard.workspaceController, clipboard.blockModels);
            copy.boundingBox = clipboard.boundingBox;
            return copy;
        } else {
            CopyResult copy = copy(clipboard.blocks);
            copy.boundingBox = clipboard.boundingBox;
            return copy;
        }
    }

    public static final void saveBlockModels(WorkspaceController workspaceController, Collection<BlockModel> blockModels) {
        if (blockModels.isEmpty()) {
            return;
        }
        clear();

        /**
         * Save a copy of blocks and connections to the clipboard in case they
         * get deleted in the meanwhile. Because, if deleted and not copied, the
         * connections between the blocks will be lost. Additionally all event
         * handlers, change listeners and bindings will be removed
         */
        CopyResult copy = copyBlockModels(workspaceController, blockModels);
        List<BlockView> blockViews = new ArrayList<>();
        for(BlockModel blockModel : blockModels) {
            BlockView blockView = workspaceController.getBlockController(blockModel).getView();
            blockViews.add(blockView);
        }
        copy.boundingBox = BlockView.getBoundingBoxOfBlocks(blockViews); // persist the bounding block, since width and height of copied blocks are zero, because they are NOT on the scene
        clipboard = copy;
    }

    public static final void save(Collection<Block> blocks) {
        if (blocks.isEmpty()) {
            return;
        }
        clear();

        /**
         * Save a copy of blocks and connections to the clipboard in case they
         * get deleted in the meanwhile. Because, if deleted and not copied, the
         * connections between the blocks will be lost. Additionally all event
         * handlers, change listeners and bindings will be removed
         */
        CopyResult copy = copy(blocks);
        copy.boundingBox = Block.getBoundingBoxOfBlocks(blocks); // persist the bounding block, since width and height of copied blocks are zero, because they are NOT on the scene
        clipboard = copy;
    }

    private static void clear() {
        if (clipboard == null) {
            return;
        }
        if (vplcore.App.BLOCK_MVC) {
            for (BlockModel block : clipboard.blockModels) {
                block.remove();
            }
        } else {
            for (Block block : clipboard.blocks) {
                block.delete();
            }
        }
        clipboard = null;
    }

    
    
    private static CopyResult copyBlockModels(WorkspaceController workspaceController, Collection<BlockModel> blocks) {
        CopyResult result = new CopyResult();
        result.workspaceController = workspaceController;

        List<ConnectionModel> alreadyCopiedConnections = new ArrayList<>();
        List<CopiedConnectionModel> copiedConnections = new ArrayList<>();

        // copy blocks
        for (BlockModel blockModel : blocks) {
            BlockModel copiedBlock = blockModel.copy();
            result.blockModels.add(copiedBlock);

            copiedBlock.layoutXProperty().set(blockModel.layoutXProperty().get());
            copiedBlock.layoutYProperty().set(blockModel.layoutYProperty().get());

            copiedConnections.add(new CopiedConnectionModel(blockModel, copiedBlock));
        }

        // copy connections
        for (CopiedConnectionModel cc : copiedConnections) {
            int counter = 0;

            for (PortModel port : cc.oldBlock.getInputPorts()) {
                for (ConnectionModel connection : port.connectedConnections) {
                    if (!alreadyCopiedConnections.contains(connection)) {

                        // start and end block are contained in selection
                        if (blocks.contains(connection.getStartPort().parentBlock)) {
                            CopiedConnectionModel cc2 = copiedConnections
                                    .stream()
                                    .filter(i -> i.oldBlock == connection.getStartPort().parentBlock)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                alreadyCopiedConnections.add(connection);
                                ConnectionModel copiedConnection = new ConnectionModel(workspaceController, cc2.newBlock.getOutputPorts().get(0), cc.newBlock.getInputPorts().get(counter));
                                result.connectionModels.add(copiedConnection);

                            }
                        } else {
                            // only end block is contained in selection
                            alreadyCopiedConnections.add(connection);
                            ConnectionModel copiedConnection = new ConnectionModel(workspaceController, connection.getStartPort(), cc.newBlock.getInputPorts().get(counter));
                            result.connectionModels.add(copiedConnection);
                        }
                    }
                }
                counter++;
            }
        }
        return result;
    }
    
    private static CopyResult copy(Collection<Block> blocks) {
        CopyResult result = new CopyResult();
        if (blocks.isEmpty()) {
            return result;
        }

        List<Connection> alreadyCopiedConnections = new ArrayList<>();
        List<CopiedConnection> copiedConnections = new ArrayList<>();

        WorkspaceController workspaceController = blocks.iterator().next().workspaceController;

        // copy blocks
        for (Block block : blocks) {
            Block copiedBlock = block.clone();
            result.blocks.add(copiedBlock);

            copiedBlock.setLayoutX(block.getLayoutX());
            copiedBlock.setLayoutY(block.getLayoutY());

            copiedConnections.add(new CopiedConnection(block, copiedBlock));
        }

        // copy connections
        for (CopiedConnection cc : copiedConnections) {
            int counter = 0;

            for (Port port : cc.oldBlock.inPorts) {
                for (Connection connection : port.connectedConnections) {
                    if (!alreadyCopiedConnections.contains(connection)) {

                        // start and end block are contained in selection
                        if (blocks.contains(connection.getStartPort().parentBlock)) {
                            CopiedConnection cc2 = copiedConnections
                                    .stream()
                                    .filter(i -> i.oldBlock == connection.getStartPort().parentBlock)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                alreadyCopiedConnections.add(connection);
                                Connection copiedConnection = new Connection(workspaceController, cc2.newBlock.outPorts.get(0), cc.newBlock.inPorts.get(counter));
                                result.connections.add(copiedConnection);

                            }
                        } else {
                            // only end block is contained in selection
                            alreadyCopiedConnections.add(connection);
                            Connection copiedConnection = new Connection(workspaceController, connection.getStartPort(), cc.newBlock.inPorts.get(counter));
                            result.connections.add(copiedConnection);
                        }
                    }
                }
                counter++;
            }
        }
        return result;
    }

}

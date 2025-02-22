package vplcore.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import vplcore.graph.util.CopiedConnectionModel;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockView;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortModel;
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
        CopyResult copy = copyBlockModels(clipboard.workspaceController, clipboard.blockModels);
        copy.boundingBox = clipboard.boundingBox;
        return copy;
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
        for (BlockModel blockModel : blockModels) {
            BlockView blockView = workspaceController.getBlockController(blockModel).getView();
            blockViews.add(blockView);
        }
        copy.boundingBox = BlockView.getBoundingBoxOfBlocks(blockViews); // persist the bounding block, since width and height of copied blocks are zero, because they are NOT on the scene
        clipboard = copy;
    }

    private static void clear() {
        if (clipboard == null) {
            return;
        }
        for (BlockModel block : clipboard.blockModels) {
            block.remove();
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
                                ConnectionModel copiedConnection = new ConnectionModel(cc2.newBlock.getOutputPorts().get(0), cc.newBlock.getInputPorts().get(counter));
                                result.connectionModels.add(copiedConnection);

                            }
                        } else {
                            // only end block is contained in selection
                            alreadyCopiedConnections.add(connection);
                            ConnectionModel copiedConnection = new ConnectionModel(connection.getStartPort(), cc.newBlock.getInputPorts().get(counter));
                            result.connectionModels.add(copiedConnection);
                        }
                    }
                }
                counter++;
            }
        }
        return result;
    }

}

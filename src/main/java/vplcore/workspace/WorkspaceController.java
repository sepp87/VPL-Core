package vplcore.workspace;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.geometry.Point2D;
import vplcore.App;
import vplcore.context.StateManager;
import vplcore.editor.BaseController;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.model.Port;
import vplcore.graph.model.VplElement;
import vplcore.graph.util.PreConnection;

/**
 *
 * @author JoostMeulenkamp
 */
public class WorkspaceController extends BaseController {

    private final StateManager state;
    private final WorkspaceModel model;
    private final WorkspaceView view;
    private final WorkspaceZoomHelper zoomHelper;
    private final WorkspaceSelectionHelper selectionHelper;

    Map<BlockModel, BlockController> blocks = new HashMap<>();

    public BlockInfoPanel activeBlockInfoPanel;
    public boolean typeSensitive = true;

    //Radial menu
    public WorkspaceController(String contextId, WorkspaceModel workspaceModel, WorkspaceView workspaceView) {
        super(contextId);
        this.state = App.getContext(contextId).getStateManager();
        this.model = workspaceModel;
        this.view = workspaceView;
        this.zoomHelper = new WorkspaceZoomHelper(model, view);
        this.selectionHelper = new WorkspaceSelectionHelper(contextId, model, view, this);

        model.addBlockModelsListener(blockModelsListener);
        model.addConnectionModelsListener(connectionModelsListener);
        model.addBlockGroupModelsListener(blockGroupModelsListener);
    }

    /**
     * BLOCKS
     */
    SetChangeListener<BlockModel> blockModelsListener = this::onBlockModelsChanged;

    private void onBlockModelsChanged(Change<? extends BlockModel> change) {
        if (change.wasAdded()) {
            addBlock(change.getElementAdded());
        } else {
            removeBlock(change.getElementRemoved());
        }
    }

    private void addBlock(BlockModel blockModel) {
        // TODO Refactor and remove since the block model should not be aware of the workspace controller, but is momentarily needed by the port model
        blockModel.workspaceController = WorkspaceController.this;

        BlockView blockView = new BlockView();
        view.getChildren().add(blockView);

        BlockController blockController = new BlockController(this, blockModel, blockView);

        blocks.put(blockModel, blockController);
    }

    private void removeBlock(BlockModel blockModel) {
        BlockController blockController = blocks.get(blockModel);
        blocks.remove(blockModel);
        view.getChildren().remove(blockController.getView());
        blockController.remove();
        // controller remove itself
    }

    /**
     * CONNECTIONS
     */
    SetChangeListener<ConnectionModel> connectionModelsListener = this::onConnectionModelsChanged;

    private void onConnectionModelsChanged(Change<? extends ConnectionModel> change) {
        if (change.wasAdded()) {
            addConnection(change.getElementAdded());
        } else {
            removeConnection(change.getElementRemoved());
        }
    }

    private void addConnection(ConnectionModel connectionModel) {
        view.getChildren().add(0, connectionModel);
    }

    private void removeConnection(ConnectionModel connectionModel) {
        view.getChildren().remove(connectionModel);

        connectionModel.removeFromCanvas();
    }

    /**
     * GROUPS
     */
    SetChangeListener<BlockGroupModel> blockGroupModelsListener = this::onBlockGroupModelsChanged;

    private void onBlockGroupModelsChanged(Change<? extends BlockGroupModel> change) {
        if (change.wasAdded()) {
            addBlockGroup(change.getElementAdded());
        } else {
            removeBlockGroup(change.getElementRemoved());
        }
    }

    private void addBlockGroup(BlockGroupModel blockGroupModel) {

    }

    private void removeBlockGroup(BlockGroupModel blockGroupModel) {

    }

    /**
     * SECTION BREAK
     */
    private PreConnection preConnection = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void initiateConnection(Port port) {
        if (preConnection == null) {
            preConnection = new PreConnection(WorkspaceController.this, port);
            view.getChildren().add(0, preConnection);
        }
    }

    // method is unneeded if createConnection catches the second click
    public void removeChild(PreConnection preConnection) {
        view.getChildren().remove(preConnection);
        this.preConnection = null;
    }

    private PreConnectionModel preConnectionModel = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void initiateConnection(PortModel portModel) {
        if (preConnectionModel == null) {
            preConnectionModel = new PreConnectionModel(WorkspaceController.this, portModel);
            view.getChildren().add(0, preConnectionModel);
        }
    }

    // method is unneeded if createConnection catches the second click
    public void removeChild(PreConnectionModel preConnectionModel) {
        view.getChildren().remove(preConnectionModel);
        this.preConnectionModel = null;
    }

    public WorkspaceModel getModel() {
        return model;
    }

    public void reset() {
        model.reset();
        view.getChildren().clear();
    }

    public void setIdle() {
        state.setIdle();
    }

    public void setSelectingBlockGroup() {
        state.setSelectingBlockGroup();
    }

    public double getZoomFactor() {
        return model.zoomFactorProperty().get();
    }

    public void zoomIn() {
        zoomHelper.zoomIn();
    }

    public void zoomOut() {
        zoomHelper.zoomOut();
    }

    public void applyZoom(double newScale, Point2D pivotPoint) {
        zoomHelper.applyZoom(newScale, pivotPoint);
    }

    public void zoomToFit() {
        if (vplcore.App.BLOCK_MVC) {
            Collection<BlockController> blockControllers = !getSelectedBlockControllers().isEmpty() ? getSelectedBlockControllers() : blocks.values();
            zoomHelper.zoomToFitBlockControllers(blockControllers);
        } else {
            Collection<Block> blocks = !getSelectedBlocks().isEmpty() ? getSelectedBlocks() : model.getBlocks();
            zoomHelper.zoomToFit(blocks);
        }
    }

    public void updateSelection(BlockController block, boolean isModifierDown) {
        selectionHelper.updateSelection(block, isModifierDown);
    }

    public void selectBlock(BlockController block) {
        selectionHelper.selectBlock(block);
    }

    public void selectAllBlocks() {
        selectionHelper.selectAllBlocks();
    }

    public void deselectAllBlocks() {
        selectionHelper.deselectAllBlocks();
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        selectionHelper.rectangleSelect(selectionMin, selectionMax);
    }

    public WorkspaceView getView() {
        return view;
    }

    public void addBlock(Block block) {
        model.addBlock(block);
        if (block.isSelected()) {
            selectionHelper.selectBlock(block);
        }
        view.getChildren().add(block);
    }

    public void selectBlock(Block block) {
        selectionHelper.selectBlock(block);
    }

    public void deselectBlock(Block block) {
        selectionHelper.deselectBlock(block);
    }

    public void selectBlock(BlockModel blockModel) {
        BlockController blockController = blocks.get(blockModel);
        selectionHelper.selectBlock(blockController);
    }

    public void deselectBlock(BlockModel blockModel) {
        BlockController blockController = blocks.get(blockModel);
        selectionHelper.deselectBlock(blockController);
    }

    public Collection<Block> getBlocks() {
        return model.getBlocks();
    }

    public Collection<Block> getSelectedBlocks() {
        return selectionHelper.getSelectedBlocks();
    }

    public Collection<BlockController> getBlockControllers() {
        return blocks.values();
    }

    public BlockController getBlockController(BlockModel blockModel) {
        return blocks.get(blockModel);
    }

    public Collection<BlockController> getSelectedBlockControllers() {
        return selectionHelper.getSelectedBlockControllers();
    }

    public Collection<Connection> getConnections() {
        return model.getConnections();
    }

    public Collection<BlockGroup> getBlockGroups() {
        return model.getBlockGroups();
    }

    public <E extends VplElement> void removeChild(E element) {
        System.out.println("WorkspaceController REIMPLEMENT " + element.getClass().getSimpleName() + " removed");
    }

    public void removeChild(ConnectionModel connectionModel) {
        System.out.println("WorkspaceController ConnectionModel removed");
        model.removeConnectionModel(connectionModel);
        view.getChildren().remove(connectionModel);
    }

    public void removeChild(Connection connection) {
        System.out.println("WorkspaceController Connection removed");
        model.removeConnection(connection);
        view.getChildren().remove(connection);
    }

    public void removeChild(Block block) {
        System.out.println("WorkspaceController " + block.getClass().getSimpleName() + " removed");
        model.removeBlock(block);
//        blocksSelectedOnWorkspace.remove(block);
        view.getChildren().remove(block);
    }

    public void removeChild(BlockGroup blockGroup) {
        System.out.println("WorkspaceController " + blockGroup.getClass().getSimpleName() + " removed");
        model.removeBlockGroup(blockGroup);
        view.getChildren().remove(blockGroup);
    }

    public void addConnection(Port startPort, Port endPort) {
        Connection connection = new Connection(this, startPort, endPort);
        addConnection(connection);
    }

    public void addConnection(Connection connection) {
        model.addConnection(connection);
        view.getChildren().add(0, connection);
    }

//    public void addConnectionModel(PortModel startPort, PortModel endPort) {
//        ConnectionModel connection = new ConnectionModel(this, startPort, endPort);
//        addConnectionModel(connection);
//    }
//
//    public void addConnectionModel(ConnectionModel connection) {
//        model.addConnectionModel(connection);
//        view.getChildren().add(0, connection);
//    }
    public void addBlockGroup() {
        if (getSelectedBlocks().size() <= 1) {
            return;
        }
        BlockGroup blockGroup = new BlockGroup(this);
        model.addBlockGroup(blockGroup);
        blockGroup.setChildBlocks(getSelectedBlocks());
        view.getChildren().add(0, blockGroup);
    }

}

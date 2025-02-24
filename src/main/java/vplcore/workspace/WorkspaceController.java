package vplcore.workspace;

import java.util.ArrayList;
import vplcore.graph.group.BlockGroupModel;
import vplcore.graph.connection.PreConnection;
import vplcore.graph.block.BlockController;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.block.BlockView;
import vplcore.graph.block.BlockModel;
import vplcore.graph.port.PortModel;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import javafx.geometry.Point2D;
import vplcore.App;
import vplcore.context.StateManager;
import vplcore.editor.BaseController;
import vplcore.graph.block.BlockModelInfoPanel;
import vplcore.graph.connection.ConnectionController;
import vplcore.graph.connection.ConnectionView;
import vplcore.graph.group.BlockGroupController;
import vplcore.graph.group.BlockGroupView;
import vplcore.graph.port.PortController;

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
    

    private final Map<BlockModel, BlockController> blocks = new HashMap<>();
    private final Map<ConnectionModel, ConnectionController> connections = new HashMap<>();
    private final Map<BlockGroupModel, BlockGroupController> blockGroups = new HashMap<>();
    private final Map<String, PortController> ports = new HashMap<>();
    
    // TODO Remove public access to info panel 
    public BlockModelInfoPanel activeBlockModelInfoPanel;
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
    
    public void registerPort(PortController portController) {
        ports.put(portController.getModel().idProperty().get(), portController);
    }
    
    public void unregisterPort(PortController portController) {
        ports.remove(portController.getModel().idProperty().get());
    }

    public PortController getPortController(String id) {
        return ports.get(id);
    }
    
    public boolean areSelectedBlocksGroupable() {
        List<BlockController> groupedBlocks = new ArrayList<>();
        Collection<BlockController> selectedBlocks = getSelectedBlockControllers();
        if (selectedBlocks.size() < 2) {
            return false;
        }
        for (BlockController block : selectedBlocks) {
            if (block.getModel().groupedProperty().get()) {
                groupedBlocks.add(block);
                return false;
            }
        }
        return true;
    }

    /**
     * BLOCKS
     */
    SetChangeListener<BlockModel> blockModelsListener = this::onBlockModelsChanged;

    private void onBlockModelsChanged(Change<? extends BlockModel> change) {
        System.out.println("WorkspaceController.onBlockModelsChanged");
        if (change.wasAdded()) {
            addBlock(change.getElementAdded());
        } else {
            removeBlock(change.getElementRemoved());
        }
    }

    private void addBlock(BlockModel blockModel) {
        System.out.println("WorkspaceController.addBlock()");
        blockModel.workspaceController = WorkspaceController.this; // TODO Refactor and remove since the block model should not be aware of the workspace controller, but is momentarily needed by the port model
        BlockView blockView = new BlockView();
        view.getChildren().add(blockView);
        BlockController blockController = new BlockController(this, blockModel, blockView);
        blocks.put(blockModel, blockController);
    }

    private void removeBlock(BlockModel blockModel) {
        System.out.println("WorkspaceController.removeBlock()");
        BlockController blockController = blocks.remove(blockModel);
        selectionHelper.deselectBlock(blockController); // deselect in case the block was selected
        view.getChildren().remove(blockController.getView());
        blockController.remove();
        // controller remove itself
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
        System.out.println("WorkspaceController.addBlockGroup()");
        BlockGroupView blockGroupView = new BlockGroupView();
        view.getChildren().add(0, blockGroupView);
        BlockGroupController blockGroupController = new BlockGroupController(this, blockGroupModel, blockGroupView);
        List<BlockController> blockControllers = new ArrayList<>();
        for (BlockModel blockModel : blockGroupModel.getBlocks()) {
            blockControllers.add(blocks.get(blockModel));
        }
        blockGroupController.setBlocks(blockControllers);
        blockGroups.put(blockGroupModel, blockGroupController);
    }

    private void removeBlockGroup(BlockGroupModel blockGroupModel) {
        System.out.println("WorkspaceController.removeBlockGroup()");
        BlockGroupController blockGroupController = blockGroups.remove(blockGroupModel);
        view.getChildren().remove(blockGroupController.getView());
        blockGroupController.remove();
    }

    /**
     * CONNECTIONS
     */
    SetChangeListener<ConnectionModel> connectionModelsListener = this::onConnectionModelsChanged;

    private void onConnectionModelsChanged(Change<? extends ConnectionModel> change) {
        System.out.println("WorkspaceController.onConnectionModelsChanged()");
        if (change.wasAdded()) {
            addConnection(change.getElementAdded());
        } else {
            removeConnection(change.getElementRemoved());
        }
    }

    private void addConnection(ConnectionModel connectionModel) {
        System.out.println("WorkspaceController.addConnection()");
        ConnectionView connectionView = new ConnectionView();
        int position = blockGroups.size() + 1; // connections should be placed above groups, otherwise the remove button is not shown
        view.getChildren().add(position, connectionView);

        ConnectionController connectionController = new ConnectionController(this, connectionModel, connectionView);
        connections.put(connectionModel, connectionController);
//        view.getChildren().add(0, connectionModel);
    }

    private void removeConnection(ConnectionModel connectionModel) {
        System.out.println("WorkspaceController.removeConnection()");
        ConnectionController connectionController = connections.remove(connectionModel);
        view.getChildren().remove(connectionController.getView());
        connectionController.remove();

//        view.getChildren().remove(connectionModel);
//        connectionModel.remove();
    }

    private PreConnection preConnection = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void initiateConnection(PortController portController) {
        System.out.println("WorkspaceController.initiateConnection()");
        if (preConnection == null) {
            preConnection = new PreConnection(WorkspaceController.this, portController);
            view.getChildren().add(0, preConnection);
        }
    }

    // method is unneeded if createConnection catches the second click
    public void removePreConnection(PreConnection preConnection) {
        System.out.println("WorkspaceController.removePreConnection()");
        view.getChildren().remove(preConnection);
        this.preConnection = null;
    }

    public WorkspaceModel getModel() {
        return model;
    }

    public void reset() {
        model.reset();
        view.getChildren().clear();
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
        Collection<BlockController> blockControllers = !getSelectedBlockControllers().isEmpty() ? getSelectedBlockControllers() : blocks.values();
        zoomHelper.zoomToFitBlockControllers(blockControllers);
    }

    public void updateSelection(BlockController block, boolean isModifierDown) {
        selectionHelper.updateSelection(block, isModifierDown);
    }

    public void selectBlock(BlockModel blockModel) {
        BlockController blockController = blocks.get(blockModel);
        selectionHelper.selectBlock(blockController);
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

    public Collection<BlockController> getBlockControllers() {
        return blocks.values();
    }

    public BlockController getBlockController(BlockModel blockModel) {
        return blocks.get(blockModel);
    }

    public Collection<BlockController> getSelectedBlockControllers() {
        return selectionHelper.getSelectedBlockControllers();
    }
}

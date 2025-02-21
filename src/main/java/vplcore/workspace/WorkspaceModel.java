package vplcore.workspace;

import vplcore.graph.group.BlockGroupModel;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.block.BlockModel;
import vplcore.graph.port.PortModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;

/**
 *
 * @author Joost
 */
public class WorkspaceModel {

    // TODO remove workspace controller here since the workspace model should not know about it
    public WorkspaceController workspaceController;

    public static final double DEFAULT_ZOOM = 1.0;
    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;
    
    private final BlockGroupIndex blockGroupIndex;

    private final DoubleProperty zoomFactor;
    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    private final ObservableSet<BlockModel> blockModels = FXCollections.observableSet();
    private final ObservableSet<ConnectionModel> connectionModels = FXCollections.observableSet();
    private final ObservableSet<BlockGroupModel> blockGroupModels = FXCollections.observableSet();

    public WorkspaceModel() {
        blockGroupIndex = new BlockGroupIndex();
        
        zoomFactor = new SimpleDoubleProperty(DEFAULT_ZOOM);
        translateX = new SimpleDoubleProperty(0.);
        translateY = new SimpleDoubleProperty(0.);
    }

    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public DoubleProperty translateXProperty() {
        return translateX;
    }

    public DoubleProperty translateYProperty() {
        return translateY;
    }

    public void resetZoomFactor() {
        zoomFactor.set(DEFAULT_ZOOM);
    }

    // Increment zoom factor by the defined step size
    public double getIncrementedZoomFactor() {
        return Math.min(MAX_ZOOM, zoomFactor.get() + ZOOM_STEP);
    }

    // Decrement zoom factor by the defined step size
    public double getDecrementedZoomFactor() {
        return Math.max(MIN_ZOOM, zoomFactor.get() - ZOOM_STEP);
    }

    public void setZoomFactor(double factor) {
        this.zoomFactor.set(Math.round(factor * 10) / 10.);
    }

    public void reset() {
        resetZoomFactor();
        translateXProperty().set(0.);
        translateYProperty().set(0.);
        blockModels.clear();
        connectionModels.clear();
        blockGroupModels.clear();
    }

    /**
     *
     * BLOCKS
     */
    public void addBlockModel(BlockModel blockModel) {
        blockModels.add(blockModel);
    }

    public void removeBlockModel(BlockModel blockModel) {
        blockModels.remove(blockModel);
        blockModel.remove();
    }

    public ObservableSet<BlockModel> getBlockModels() {
        return FXCollections.unmodifiableObservableSet(blockModels);
    }

    public void addBlockModelsListener(SetChangeListener<BlockModel> listener) {
        blockModels.addListener(listener);
    }

    public void removeBlockModelsListener(SetChangeListener<BlockModel> listener) {
        blockModels.removeListener(listener);
    }

    /**
     *
     * CONNECTIONS
     */
    public void addConnectionModel(PortModel startPort, PortModel endPort) {
        ConnectionModel connectionModel = new ConnectionModel(workspaceController, startPort, endPort);
        addConnectionModel(connectionModel);
    }

    public void addConnectionModel(ConnectionModel connectionModel) {
        connectionModels.add(connectionModel);
    }

    public void removeConnectionModel(ConnectionModel connectionModel) {
        connectionModels.remove(connectionModel);
    }

    public ObservableSet<ConnectionModel> getConnectionModels() {
        return FXCollections.unmodifiableObservableSet(connectionModels);
    }

    public void addConnectionModelsListener(SetChangeListener<ConnectionModel> listener) {
        connectionModels.addListener(listener);
    }

    public void removeConnectionModelsListener(SetChangeListener<ConnectionModel> listener) {
        connectionModels.removeListener(listener);
    }

    /**
     *
     * GROUPS
     */
    public void removeBlockGroupModel(BlockGroupModel blockGroupModel) {
        System.out.println("WorkspaceModel.removeBlockGroupModel()");
        blockGroupModels.remove(blockGroupModel);
        blockGroupModel.remove();
    }

    public ObservableSet<BlockGroupModel> getBlockGroupModels() {
        return FXCollections.unmodifiableObservableSet(blockGroupModels);
    }

    public void addBlockGroupModelsListener(SetChangeListener<BlockGroupModel> listener) {
        blockGroupModels.addListener(listener);
    }

    public void removeBlockGroupModelsListener(SetChangeListener<BlockGroupModel> listener) {
        blockGroupModels.removeListener(listener);
    }

    public void addBlockGroupModel(BlockGroupModel blockGroupModel) {
        System.out.println("WorkspaceModel.addBlockGroupModel()");
        blockGroupModels.add(blockGroupModel);
    }

    public void removeBlockFromGroup(BlockModel blockModel) {
        System.out.println("WorkspaceModel.removeBlockFromGroup()");
        BlockGroupModel blockGroupModel = blockGroupIndex.getBlockGroup(blockModel);
        if (blockGroupModel != null) {
            blockGroupModel.removeBlock(blockModel);
            if (blockGroupModel.getBlocks().size() <= 1) {
                removeBlockGroupModel(blockGroupModel);
            }
        }
    }

    public BlockGroupIndex getBlockGroupIndex() {
        return blockGroupIndex;
    }

}

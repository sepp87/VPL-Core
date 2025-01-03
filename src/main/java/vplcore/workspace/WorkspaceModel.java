package vplcore.workspace;

import java.util.Collection;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import vplcore.graph.model.Connection;

/**
 *
 * @author Joost
 */
public class WorkspaceModel {

    // TODO remove workspace controller here since the workspace model should not know about it
    public WorkspaceController workspaceController;

    public static final double DFEAULT_ZOOM = 1.0;
    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    private final DoubleProperty zoomFactor;
    private final DoubleProperty translateX;
    private final DoubleProperty translateY;

    private final ObservableSet<Block> blocks = FXCollections.observableSet();
    private final ObservableSet<Connection> connections = FXCollections.observableSet();
    private final ObservableSet<BlockGroup> blockGroups = FXCollections.observableSet();

    private final ObservableSet<BlockModel> blockModels = FXCollections.observableSet();
    private final ObservableSet<ConnectionModel> connectionModels = FXCollections.observableSet();
    private final ObservableSet<BlockGroupModel> blockGroupModels = FXCollections.observableSet();
    

    public WorkspaceModel() {
        zoomFactor = new SimpleDoubleProperty(DFEAULT_ZOOM);
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
        zoomFactor.set(DFEAULT_ZOOM);
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

    public Collection<Block> getBlocks() {
        return blocks;
    }

    public Collection<Connection> getConnections() {
        return connections;
    }

    public Collection<BlockGroup> getBlockGroups() {
        return blockGroups;
    }

    public void addBlock(Block block) {
        blocks.add(block);
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public void addBlockGroup(BlockGroup blockGroup) {
        blockGroups.add(blockGroup);
    }

    public void removeBlock(Block block) {
        blocks.remove(block);
    }

    public void removeConnection(Connection connection) {
        connections.remove(connection);
    }

    public void removeBlockGroup(BlockGroup blockGroup) {
        blockGroups.remove(blockGroup);
    }

    public void reset() {
        resetZoomFactor();
        translateXProperty().set(0.);
        translateYProperty().set(0.);
        blocks.clear();
        connections.clear();
        blockGroups.clear();
    }

    public void addBlockModel(BlockModel blockModel) {
        blockModels.add(blockModel);
    }

    public void removeBlockModel(BlockModel blockModel) {
        blockModels.remove(blockModel);
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

    public ObservableSet<BlockGroupModel> getBlockGroupModels() {
        return FXCollections.unmodifiableObservableSet(blockGroupModels);
    }
    
    public void addBlockGroupModel(BlockGroupModel blockGroupModel) {
        blockGroupModels.add(blockGroupModel);
    }

}

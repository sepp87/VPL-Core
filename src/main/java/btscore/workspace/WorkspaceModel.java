package btscore.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.block.BlockModel;
import btscore.graph.port.PortModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import btscore.App;

/**
 *
 * @author Joost
 */
public class WorkspaceModel {

    public static final double DEFAULT_ZOOM = 1.0;
    public static final double MAX_ZOOM = 1.5;
    public static final double MIN_ZOOM = 0.3;
    public static final double ZOOM_STEP = 0.1;

    private final BlockGroupIndex blockGroupIndex = new BlockGroupIndex();
    private final AutoConnectIndex wirelessIndex = new AutoConnectIndex();

    private final BooleanProperty savable = new SimpleBooleanProperty(this, "savable", false);
    private final ObjectProperty<File> file = new SimpleObjectProperty(this, "file", null);

    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(DEFAULT_ZOOM);
    private final DoubleProperty translateX = new SimpleDoubleProperty(0.);
    private final DoubleProperty translateY = new SimpleDoubleProperty(0.);

    private final ObservableSet<BlockModel> blockModels = FXCollections.observableSet();
    private final ObservableSet<ConnectionModel> connectionModels = FXCollections.observableSet();
    private final ObservableSet<BlockGroupModel> blockGroupModels = FXCollections.observableSet();
    private final ObservableMap<Class<?>, List<PortModel>> dataTransmittors = FXCollections.observableHashMap();
    
    public AutoConnectIndex getWirelessIndex(){
        return wirelessIndex;
    }

    public BooleanProperty savableProperty() {
        return savable;
    }

    public ObjectProperty<File> fileProperty() {
        return file;
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
        List<PortModel> transmittingPorts = blockModel.getTransmittingPorts();
        for (PortModel port : transmittingPorts) {
            dataTransmittors.computeIfAbsent(port.getDataType(), dataType -> new ArrayList<>()).add(port);
        }
        blockModels.add(blockModel);
        blockModel.setActive(true); // blocks are first activated when added to the workspace to avoid unnecessary processing e.g. during copy & paste

    }

    public void removeBlockModel(BlockModel blockModel) {
        List<PortModel> transmittingPorts = blockModel.getTransmittingPorts();
        for (PortModel port : transmittingPorts) {
            List<PortModel> list = dataTransmittors.get(port.getDataType());
            if (list != null) {
                list.remove(port);
            }
        }
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
    public ConnectionModel addConnectionModel(PortModel startPort, PortModel endPort) {
        ConnectionModel connectionModel = new ConnectionModel(startPort, endPort);
        addConnectionModel(connectionModel);
        return connectionModel;
    }

    public void addConnectionModel(ConnectionModel connectionModel) {
        connectionModels.add(connectionModel);
    }

    public void removeConnectionModel(ConnectionModel connectionModel) {
        connectionModels.remove(connectionModel);
        connectionModel.remove();
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

    public List<ConnectionModel> removeConnectionModels(BlockModel blockModel) {
        if (App.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.removeConnectionModels()");
        }
        List<ConnectionModel> connections = blockModel.getConnections();
        for (ConnectionModel connection : connections) {
            removeConnectionModel(connection);
        }
        return connections;
    }

    /**
     *
     * GROUPS
     */
    public void removeBlockGroupModel(BlockGroupModel blockGroupModel) {
        if (App.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.removeBlockGroupModel()");
        }
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
        if (App.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.addBlockGroupModel()");
        }
        blockGroupModels.add(blockGroupModel);
    }

    public BlockGroupModel removeBlockFromGroup(BlockModel blockModel) {
        if (App.LOG_METHOD_CALLS) {
            System.out.println("WorkspaceModel.removeBlockFromGroup()");
        }
        BlockGroupModel blockGroupModel = blockGroupIndex.getBlockGroup(blockModel);
        if (blockGroupModel != null) {
            blockGroupModel.removeBlock(blockModel);
            if (blockGroupModel.getBlocks().size() <= 1) {
                removeBlockGroupModel(blockGroupModel);
            }
        }
        return blockGroupModel;
    }

    public BlockGroupIndex getBlockGroupIndex() {
        return blockGroupIndex;
    }

    public BlockGroupModel getBlockGroup(BlockModel block) {
        return blockGroupIndex.getBlockGroup(block);
    }

}

package btscore.graph.block;

import static java.lang.Thread.sleep;
import btscore.graph.base.BaseModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import btscore.graph.port.PortModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import btsxml.BlockTag;
import btscore.graph.block.ExceptionPanel.BlockException;
import btscore.graph.connection.ConnectionModel;
import btscore.graph.port.PortType;
import btscore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public abstract class BlockModel extends BaseModel {

//    protected final WorkspaceModel workspace;
    protected final ObservableList<PortModel> inputPorts = FXCollections.observableArrayList();
    protected final ObservableList<PortModel> outputPorts = FXCollections.observableArrayList();
    protected final ObservableList<BlockException> exceptions = FXCollections.observableArrayList();

    private final BooleanProperty grouped = new SimpleBooleanProperty(false);

    public BlockModel() {
        this.active.addListener(activeListener);
    }

    protected abstract void initialize();

    private final ChangeListener<Boolean> activeListener = (b, o, n) -> onActiveChanged();

    /**
     * This method is called when the active state changes. When this block is
     * activated processSafely() is called in two cases; if this block has a
     * default output or if there are incoming connections. In case this block
     * was deactivated all outputs are set to null. Override this change
     * listener to modify its behaviour accordingly. Call
     * super.onActiveChanged() last if you want to continue with the default
     * behaviour.
     */
    protected void onActiveChanged() {
        if (!this.isActive()) {
            for (PortModel output : outputPorts) {
                output.setData(null);
            }
            return;
        }
        if (this.getMetadata().hasDefaultOutput() || inputPorts.isEmpty()) {
//        if (this.getMetadata().hasDefaultOutput() || inputPorts.isEmpty() || inputPorts.stream().anyMatch(PortModel::isActive)) {
            processSafely();
        }
    }

    public ObservableList<BlockException> getExceptions() {
        return exceptions;
    }

    public List<ConnectionModel> getConnections() {
        List<ConnectionModel> result = new ArrayList<>();
        for (PortModel port : inputPorts) {
            result.addAll(port.getConnections());
        }
        for (PortModel port : outputPorts) {
            result.addAll(port.getConnections());
        }
        return result;
    }

    public BooleanProperty groupedProperty() {
        return grouped;
    }

    /**
     * Safeguard against null data to ensure errors are shown. If there was no
     * previous connection, the data was already null. Since the new incoming
     * data is also null, onInputDataChanged wasn't triggered. However, now that
     * the user is interacting with the block, we should clearly indicate that
     * input data is missing. Therefor, processSafely is triggered in case of
     * null.
     */
    public void onIncomingConnectionAdded(Object data) {
        if (data == null) {
            processSafely();
        }
    }

    /**
     * Safeguard against null data to ensure obsolete errors are cleared. If the
     * removed data was already null, it remains null and onInputDataChanged
     * won’t be triggered. Since the user is actively removing the connection,
     * any related errors are no longer relevant. Therefor, processSafely is
     * triggered in case of null.
     */
    public void onIncomingConnectionRemoved(Object data) {
        if (data == null) {
            processSafely();
        }
    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public void processSafely() {
        Set<BlockException> previousExceptions = new HashSet<>(exceptions);

        // Ensure processing only happens when active
        if (!this.isActive()) {
            return;
        }

        try {
            process();
        } catch (Exception exception) {
            BlockException blockException = new BlockException(null, ExceptionPanel.Severity.ERROR, exception);
            exceptions.add(blockException);
            Logger.getLogger(BlockModel.class.getName()).log(Level.SEVERE, null, exception);
        }

        // When there are no more incoming connections, all exceptions should be cleared, since there is nothing to process
        if (!inputPorts.isEmpty() && inputPorts.stream().noneMatch(PortModel::isActive)) {
            exceptions.clear();
            return;
        }

        if (!(this instanceof MethodBlock)) {
            exceptions.removeAll(previousExceptions);
        }

    }

    protected abstract void process() throws Exception;

    public abstract BlockModel copy();

    public ObservableList<PortModel> getInputPorts() {
        return FXCollections.unmodifiableObservableList(inputPorts);
    }

    public List<PortModel> getReceivingPorts() {
        return getWirelessPorts(inputPorts);
    }

    public ObservableList<PortModel> getOutputPorts() {
        return FXCollections.unmodifiableObservableList(outputPorts);
    }

    public List<PortModel> getTransmittingPorts() {
        return getWirelessPorts(outputPorts);
    }

    private List<PortModel> getWirelessPorts(List<PortModel> ports) {
        List<PortModel> result = new ArrayList<>();
        for (PortModel port : ports) {
            if (port.wirelessProperty().get()) {
                result.add(port);
            }
        }
        return result;
    }

    public PortModel addInputPort(String name, Class<?> type) {
        return addInputPort(name, type, false);
    }

    public PortModel addInputPort(String name, Class<?> type, boolean isAutoConnectable) {
        PortModel port = new PortModel(name, PortType.INPUT, type, this, false);
        port.dataProperty().addListener(inputDataListener);
        port.wirelessProperty().set(isAutoConnectable);
        inputPorts.add(port);
        return port;
    }

    private final ChangeListener<Object> inputDataListener = this::onInputDataChanged;

    private void onInputDataChanged(ObservableValue b, Object o, Object n) {
        processSafely();
    }

    public PortModel addOutputPort(String name, Class<?> type) {
        return addOutputPort(name, type, false);
    }
    public PortModel addOutputPort(String name, Class<?> type, boolean isAutoConnectable) {
        PortModel port = new PortModel(name, PortType.OUTPUT, type, this, true);
        port.wirelessProperty().set(isAutoConnectable);
        outputPorts.add(port);
        return port;
    }

    public void serialize(BlockTag xmlTag) {
        xmlTag.setType(this.getClass().getAnnotation(BlockMetadata.class).identifier());
        xmlTag.setUUID(idProperty().get());
        xmlTag.setX(layoutXProperty().get());
        xmlTag.setY(layoutYProperty().get());
        if (resizableProperty().get()) {
            xmlTag.setWidth(widthProperty().get());
            xmlTag.setHeight(heightProperty().get());
        }
    }

    public void deserialize(BlockTag xmlTag) {
        this.id.set(xmlTag.getUUID());
        layoutXProperty().set(xmlTag.getX());
        layoutYProperty().set(xmlTag.getY());
        if (resizableProperty().get()) {
            Double width = xmlTag.getWidth();
            Double height = xmlTag.getHeight();
            if (width == null || height == null) {
                return;
            }
            widthProperty().set(width);
            heightProperty().set(height);
        }
    }

    @Override
    public void remove() {
        // clean up routine for sub-classes
        onRemoved();

        // stop processing
        setActive(false);

        // remove listeners
        this.active.removeListener(activeListener);
        for (PortModel port : inputPorts) {
            port.dataProperty().removeListener(inputDataListener);
            port.remove();
        }
        for (PortModel port : outputPorts) {
            port.remove();
        }

        super.remove();
    }

    protected abstract void onRemoved();

    @Override
    public void revive() {
        // add listeners
        this.active.addListener(activeListener);
        for (PortModel port : inputPorts) {
            port.dataProperty().addListener(inputDataListener);
            port.revive();
        }
        for (PortModel port : outputPorts) {
            port.revive();
        }

        initialize();

        super.revive();
    }

    public BlockMetadata getMetadata() {
        BlockMetadata metadata = this.getClass().getAnnotation(BlockMetadata.class);
        return metadata;
    }

}

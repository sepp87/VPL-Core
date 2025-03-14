package vplcore.graph.block;

import vplcore.graph.base.BaseModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import vplcore.graph.port.PortModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.ExceptionPanel.BlockException;
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortType;
import vplcore.graph.util.MethodBlock;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public abstract class BlockModel extends BaseModel {

    protected final WorkspaceModel workspace;

    protected final ObservableList<PortModel> inputPorts = FXCollections.observableArrayList();
    protected final ObservableList<PortModel> outputPorts = FXCollections.observableArrayList();
    protected final ObservableList<BlockException> exceptions = FXCollections.observableArrayList();

    private final BooleanProperty grouped = new SimpleBooleanProperty(false);

    public BlockModel(WorkspaceModel workspace) {
        this.workspace = workspace;
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

    // should be private or protected and should be a listener to inputPorts
    public void onIncomingConnectionAdded() {
        processSafely();
    }

    public void onIncomingConnectionRemoved() {
        processSafely();
    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public final void processSafely() {
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
        
        exceptions.removeAll(previousExceptions);
    }

    protected abstract void process() throws Exception;

    public abstract BlockModel copy();

    public ObservableList<PortModel> getInputPorts() {
        return FXCollections.unmodifiableObservableList(inputPorts);
    }

    public ObservableList<PortModel> getOutputPorts() {
        return FXCollections.unmodifiableObservableList(outputPorts);
    }

    public PortModel addInputPort(String name, Class<?> type) {
        PortModel port = new PortModel(name, PortType.INPUT, type, this, false);
        port.dataProperty().addListener(inputDataListener);
        inputPorts.add(port);
        return port;
    }

    private final ChangeListener<Object> inputDataListener = this::onInputDataChanged;

    private void onInputDataChanged(ObservableValue b, Object o, Object n) {
        processSafely();
        // TODO try to process 
        // TODO if successful, remove any shown block expections
        // TODO when exception thrown by process
        // TODO create and set active block exception 
    }

    public PortModel addOutputPort(String name, Class<?> type) {
        PortModel port = new PortModel(name, PortType.OUTPUT, type, this, true);
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

        onRemoved();
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

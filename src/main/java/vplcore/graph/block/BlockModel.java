package vplcore.graph.block;

import vplcore.graph.base.BaseModel;
import java.util.ArrayList;
import java.util.List;
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
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public abstract class BlockModel extends BaseModel {

    protected final WorkspaceModel workspace;

    protected final ObservableList<PortModel> inputPorts = FXCollections.observableArrayList();
    protected final ObservableList<PortModel> outputPorts = FXCollections.observableArrayList();
    private final ObservableList<BlockException> exceptions = FXCollections.observableArrayList();

    private final BooleanProperty grouped = new SimpleBooleanProperty(false);

    public BlockModel(WorkspaceModel workspace) {
        this.workspace = workspace;
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

    // should be private or protected and should be a listener to outputPorts
    public void onIncomingConnectionRemoved() {
        processSafely();
        // previously called handleIncomingConnectionRemoved and overridden by TexBlock 
    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public final void processSafely() {
        // Remove exceptions if there were any
        exceptions.clear();

        try {
            process();
        } catch (Exception exception) {
            BlockException blockException = new BlockException(null, ExceptionPanel.Severity.ERROR, exception);
            exceptions.add(blockException);
            Logger.getLogger(BlockModel.class.getName()).log(Level.SEVERE, null, exception);
        }
    }

    public abstract void process() throws Exception;

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
            widthProperty().set(xmlTag.getWidth());
            heightProperty().set(xmlTag.getHeight());
        }
    }

    public void remove() {
        // remove listeners
        for (PortModel port : inputPorts) {
            port.dataProperty().removeListener(inputDataListener);
        }

        super.remove();
    }

}

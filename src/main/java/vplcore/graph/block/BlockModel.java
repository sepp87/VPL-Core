package vplcore.graph.block;

import vplcore.graph.base.BaseModel;
import java.util.ArrayList;
import java.util.List;
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
import vplcore.graph.connection.ConnectionModel;
import vplcore.graph.port.PortType;
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author Joost
 */
public abstract class BlockModel extends BaseModel {

    // TODO remove since the block model should not be aware of the workspace controller, but is momentarily needed by the port model
    public WorkspaceController workspaceController;

    protected WorkspaceModel workspace;

    protected ObservableList<PortModel> inputPorts = FXCollections.observableArrayList();
    protected ObservableList<PortModel> outputPorts = FXCollections.observableArrayList();

    private final BooleanProperty grouped = new SimpleBooleanProperty(false);

    public BlockModel(WorkspaceModel workspace) {
        this.workspace = workspace;
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
        process();
    }

    // should be private or protected and should be a listener to outputPorts
    public void onIncomingConnectionRemoved() {
        process();
        // previously called handleIncomingConnectionRemoved and overridden by TexBlock 
    }

    public abstract Region getCustomization();

    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    public abstract void process();

    public abstract BlockModel copy();

    public ObservableList<PortModel> getInputPorts() {
        return FXCollections.unmodifiableObservableList(inputPorts);
    }

    public ObservableList<PortModel> getOutputPorts() {
        return FXCollections.unmodifiableObservableList(outputPorts);
    }

    public PortModel addInputPort(String name, Class<?> type) {
        PortModel port = new PortModel(name, PortType.IN, type, this, false);
        port.multiDockAllowed = false;
        port.dataProperty().addListener(inputDataListener);
        inputPorts.add(port);
        return port;
    }

    private final ChangeListener<Object> inputDataListener = this::onInputDataChanged;

    private void onInputDataChanged(ObservableValue b, Object o, Object n) {
        process();
        // TODO try to process 
        // TODO if successful, remove any shown block expections
        // TODO when exception thrown by process
        // TODO create and set active block exception 
    }

    public PortModel addOutputPort(String name, Class<?> type) {
        PortModel port = new PortModel(name, PortType.OUT, type, this, true);
        port.multiDockAllowed = true;
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
        id.set(xmlTag.getUUID());
        layoutXProperty().set(xmlTag.getX());
        layoutYProperty().set(xmlTag.getY());
        if (resizableProperty().get()) {
            widthProperty().set(xmlTag.getWidth());
            heightProperty().set(xmlTag.getHeight());
        }
    }

    public void remove() {
        super.remove();
        // remove listeners and bindings
    }

}

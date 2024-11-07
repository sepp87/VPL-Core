package vplcore.workspace;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

/**
 * Extension idea - allow multiple incoming connections e.g. to join strings,
 * lists or whatever (multipleIncomingAllowed)
 *
 * @author Joost
 */
public class PortModel {

    // 
    private final StringProperty name = new SimpleStringProperty();
    private final ObjectProperty<Object> data = new SimpleObjectProperty(null);
    private final ObjectProperty<Class> dataType = new SimpleObjectProperty(); // e.g. String, Number, int, double, float
    private final ObjectProperty<Class> dataStructure = new SimpleObjectProperty(); // e.g. List, Array, Map, Set

    private final ObservableSet<ConnectionModel> outgoing;
    private final ObjectProperty<ConnectionModel> incoming;

    public PortModel(String name, Class dataType, Class dataStructure, boolean isInput) {
        this.name.set(name);
        this.dataType.set(dataType);
        this.dataStructure.set(dataStructure);

        this.outgoing = isInput ? null : FXCollections.observableSet();
        this.incoming = isInput ? new SimpleObjectProperty() : null;
    }

    public boolean isInput() {
        return outgoing == null;
    }

    public boolean isOutput() {
        return outgoing != null;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<Object> dataProperty() {
        return data;
    }

    public ObjectProperty<Class> dataTypeProperty() {
        return dataType;
    }

    public void addConnection(ConnectionModel connectionModel) {
        if (isInput()) {
            incoming.set(connectionModel);
        } else {
            outgoing.add(connectionModel);
        }
    }

    public void removeConnection(ConnectionModel connectionModel) {
        if (isInput()) {
            incoming.set(null);
            data.set(null); // TODO might want to revert to default data here
        } else {
            outgoing.remove(connectionModel);
        }
    }

    public void remove() {
        if (isInput()) {
            incoming.get().remove();
        } else {
            for (ConnectionModel connection : outgoing) {
                connection.remove();
            }
        }
    }
}

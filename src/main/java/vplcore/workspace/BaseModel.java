package vplcore.workspace;

import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Joost
 */
public class BaseModel implements Comparable<BaseModel> {

    private final BaseModel parent;
    protected final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty layoutX = new SimpleDoubleProperty(-1);
    private final DoubleProperty layoutY = new SimpleDoubleProperty(-1);
    private final DoubleProperty width = new SimpleDoubleProperty(-1);
    private final DoubleProperty height = new SimpleDoubleProperty(-1);
    private final BooleanProperty resizable = new SimpleBooleanProperty(false);
    private final BooleanProperty active = new SimpleBooleanProperty(false);
    private final BooleanProperty removed = new SimpleBooleanProperty(false);

    public BaseModel() {
        this.parent = null;
        this.id.set(UUID.randomUUID().toString());
    }

    public BaseModel(BaseModel parent) {
        this.parent = parent;
        this.id.set(UUID.randomUUID().toString());
    }

    public ReadOnlyStringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public DoubleProperty layoutXProperty() {
        return layoutX;
    }

    public DoubleProperty layoutYProperty() {
        return layoutY;
    }
    
    public DoubleProperty widthProperty() {
        return width;
    }
    
    public DoubleProperty heightProperty() {
        return height;
    }
    
    public BooleanProperty resizableProperty() {
        return resizable;
    }

    public BooleanProperty activeProperty() {
        return active;
    }

    public BooleanProperty removedProperty() {
        return removed;
    }

    public void remove() {
        removed.set(true);
//        for (PortModel port : model.inputPorts) {
//            port.dataProperty().removeListener(portDataChangedListener);
//            port.delete();
//        }
//        inPorts.clear();
//        outPorts.clear();
//        controls.clear();
    }

    public void serialize() {

    }

    public void deserialize() {

    }

    public void select() {

    }

    public void deselect() {

    }

    @Override
    public int compareTo(BaseModel o) {
        return this.id.get().compareTo(o.id.get());
    }
}

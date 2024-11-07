package vplcore.workspace;

import java.util.UUID;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
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
    private final ReadOnlyStringProperty id;
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty layoutX = new SimpleDoubleProperty();
    private final DoubleProperty layoutY = new SimpleDoubleProperty();
    private final BooleanProperty resizable = new SimpleBooleanProperty(false);
    private final BooleanProperty active = new SimpleBooleanProperty(false);
    
    
    public BaseModel() {
        this.parent = null;
        this.id = new ReadOnlyStringWrapper(UUID.randomUUID().toString());
    }

    public BaseModel(BaseModel parent) {
        this.parent = parent;
        this.id = new ReadOnlyStringWrapper(UUID.randomUUID().toString());
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
    
    public BooleanProperty activeProperty() {
        return active;
    }

    public void remove() {

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

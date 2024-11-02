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
public class BlockModel {

    private final ReadOnlyStringProperty id;
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty layoutX = new SimpleDoubleProperty();
    private final DoubleProperty layoutY = new SimpleDoubleProperty();
    private final BooleanProperty resizable = new SimpleBooleanProperty(false); 
    
   
    public BlockModel() {
        this.id = new ReadOnlyStringWrapper(UUID.randomUUID().toString());

    }

    public ReadOnlyStringProperty idProperty() {
        return id;
    }

    public StringProperty nameProperty() {
        return name;
    }

}

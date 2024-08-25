package vplcore;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author JoostMeulenkamp
 */
public class EventBlaster {

    private final PropertyChangeSupport changeHandler;
    private final Map<ObservableValue, String> propertyNameMap;
    private final ChangeListener changeListener;

    public EventBlaster(Object bean) {
        this.changeHandler = new PropertyChangeSupport(bean);
        this.propertyNameMap = new HashMap();
        this.changeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String propertyName = EventBlaster.this.propertyNameMap.get(observable);
                EventBlaster.this.changeHandler.firePropertyChange(propertyName, oldValue, newValue);
            }
        };
    }

    /**
     * Set a property so this objects blasts events when they change
     *
     * @param property the property to set
     */
    public void set(ReadOnlyProperty property) {
        set(property.getName(), property);
    }

    /**
     * Set a property so this objects blasts events when they change
     *
     * @param propertyName the property name to overwrite
     * @param property the property to set
     */
    public void set(String propertyName, ReadOnlyProperty property) {
        if (!this.propertyNameMap.containsKey(property)) {
            propertyNameMap.put(property, propertyName);
            property.addListener(this.changeListener);
        }
    }

    public void add(PropertyChangeListener listener) {
        this.changeHandler.addPropertyChangeListener(listener);
    }

    public void add(String propertyName, PropertyChangeListener listener) {
        this.changeHandler.addPropertyChangeListener(propertyName, listener);
    }

    public void remove(PropertyChangeListener listener) {
        this.changeHandler.removePropertyChangeListener(listener);
    }

    public void remove(String propertyName, PropertyChangeListener listener) {
        this.changeHandler.removePropertyChangeListener(propertyName, listener);

    }
}

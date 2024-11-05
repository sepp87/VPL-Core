package vplcore.editor;

import vplcore.context.EditorMode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Joost
 */
public class EditorModel {

    private final ObjectProperty<EditorMode> mode = new SimpleObjectProperty<>(EditorMode.IDLE_MODE);

    public EditorModel() {
        mode.addListener(this::printMode);
    }

    public ObjectProperty<EditorMode> modeProperty() {
        return mode;
    }

    private void printMode(Object b, Object o, Object n) {
        System.out.println(n);
    }
}

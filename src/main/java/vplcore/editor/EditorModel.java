package vplcore.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Joost
 */
public class EditorModel {

    private final ObjectProperty<EditorMode> mode = new SimpleObjectProperty<>(EditorMode.IDLE_MODE);

    public ObjectProperty<EditorMode> modeProperty() {
        return mode;
    }

}

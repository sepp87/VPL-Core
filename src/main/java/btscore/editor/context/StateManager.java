package btscore.editor.context;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import btscore.App;

/**
 *
 * @author Joost
 */
public class StateManager {

    private final ObjectProperty<EditorMode> mode = new SimpleObjectProperty<>(EditorMode.IDLE_MODE);

    public StateManager() {
        mode.addListener(this::printMode);
    }

    public ObjectProperty<EditorMode> modeProperty() {
        return mode;
    }

    private void printMode(Object b, Object o, Object n) {
        if (App.LOG_EDITOR_STATE) {
            System.out.println("State " + n);
        }
    }

    public boolean isIdle() {
        return mode.get() == EditorMode.IDLE_MODE;
    }

    public boolean isPanning() {
        return mode.get() == EditorMode.PAN_MODE;
    }

    public boolean isSelecting() {
        return mode.get() == EditorMode.SELECTION_MODE;
    }

    public boolean isZooming() {
        return mode.get() == EditorMode.ZOOM_MODE;
    }

    public boolean isAwaitingRadialMenu() {
        return mode.get() == EditorMode.RADIAL_MENU_MODE;
    }

    public boolean isAwaitingBlockSearch() {
        return mode.get() == EditorMode.BLOCK_SEARCH_MODE;
    }

    public boolean isSelectingBlockGroup() {
        return mode.get() == EditorMode.GROUP_SELECTION_MODE;
    }

    public void setIdle() {
        mode.set(EditorMode.IDLE_MODE);
    }

    public void setPanning() {
        mode.set(EditorMode.PAN_MODE);
    }

    public void setSelecting() {
        mode.set(EditorMode.SELECTION_MODE);
    }

    public void setZooming() {
        mode.set(EditorMode.ZOOM_MODE);
    }

    public void setAwaitingRadialMenu() {
        mode.set(EditorMode.RADIAL_MENU_MODE);
    }

    public void setAwaitingBlockSearch() {
        mode.set(EditorMode.BLOCK_SEARCH_MODE);
    }

    public void setSelectingBlockGroup() {
        mode.set(EditorMode.GROUP_SELECTION_MODE);
    }

}

package btscore.editor.context;

import java.util.UUID;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import btscore.editor.EditorView;
import static btscore.util.EditorUtils.onFreeSpace;
import btscore.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class EditorContext {

    private final String id;
    private final EditorView editorView;
    private final WorkspaceView workspaceView;
    private final StateManager stateManager;
    private ActionManager actionManager;
    private EventRouter eventRouter;
    private Point2D mousePosition = new Point2D(0, 0);

    private final ChangeListener<Object> setupMouseTracking = this::setupMouseTracking;
    private final EventHandler<MouseEvent> trackMouse = this::trackMouse;
//    private final EventHandler<KeyEvent> trackKeyboard = this::trackKeyboard;

    public EditorContext(EditorView editorView, WorkspaceView workspaceView) {
        this.id = UUID.randomUUID().toString();
        this.editorView = editorView;
        this.workspaceView = workspaceView;
        this.stateManager = new StateManager();
        editorView.sceneProperty().addListener(setupMouseTracking);
    }

    private void setupMouseTracking(Object b, Object o, Object n) {
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, trackMouse);
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, trackMouse);
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, trackMouse);
        editorView.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, trackMouse);
//        editorView.getScene().addEventFilter(MouseEvent.ANY, trackMouseAndKeyboard);
//        editorView.getScene().addEventFilter(KeyEvent.ANY, trackKeyboard);
    }

    private void trackMouse(MouseEvent event) {
        mousePosition = new Point2D(event.getSceneX(), event.getSceneY());
        
        boolean isEditorFocused = editorView.getScene().focusOwnerProperty().get() instanceof EditorView;
        if (!isEditorFocused && onFreeSpace(event)) {
            editorView.requestFocus();
        }
    }

    public String getId() {
        return id;
    }

    public void initializeActionManager(ActionManager actionManager) {
        if (this.actionManager == null) {
            this.actionManager = actionManager;
        }
    }

    public void initializeStateManager() {

    }

    public void initializeEventRouter(EventRouter eventRouter) {
        if (this.eventRouter == null) {
            this.eventRouter = eventRouter;
        }
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public EventRouter getEventRouter() {
        return eventRouter;
    }

    public Point2D getMousePositionOnScene() {
        return mousePosition;
    }

    public Point2D getMousePositionOnWorkspace() {
        return workspaceView.sceneToLocal(mousePosition);
    }

    public Point2D sceneToWorkspace(Point2D sceneCoordinates) {
        return workspaceView.sceneToLocal(sceneCoordinates);
    }

    public void returnFocusToEditor() {
        editorView.requestFocus();
    }

}

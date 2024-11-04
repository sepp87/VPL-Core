package vplcore.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import vplcore.editor.radialmenu.RadialMenuController;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class EditorController {

    private final WorkspaceController workspace;
    private final RadialMenuController radialMenuController;
    private final ZoomController zoomController;
    private final PanController panController;
    private final KeyboardController keyboardController;
    private final SelectionRectangleController selectionRectangleController;
    private final BlockSearchController blockSearchController;
    private final EditorView view;

    private final ObjectProperty<Point2D> mousePositionOnScene;

    private final EventHandler<MouseEvent> mouseMovedHandler = this::handleMouseMoved;
    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;
    private final EventHandler<MouseEvent> mousePressedHandler = this::handleMousePressed;
    private final EventHandler<MouseEvent> mouseDraggedHandler = this::handleMouseDragged;
    private final EventHandler<MouseEvent> mouseReleasedHandler = this::handleMouseReleased;
    private final EventHandler<ScrollEvent> scrollStartedHandler = this::handleScrollStarted;
    private final EventHandler<ScrollEvent> scrollHandler = this::handleScroll;
    private final EventHandler<ScrollEvent> scrollFinishedHandler = this::handleScrollFinished;
    private final EventHandler<KeyEvent> keyPressedHandler = this::handleKeyPressed;

    public EditorController(EditorView editorView, RadialMenuController radialMenuController, WorkspaceController workspace, ZoomController zoomController, PanController panController, KeyboardController keyboardController, SelectionRectangleController selectionRectangleController, BlockSearchController blockSearchController) {
        this.workspace = workspace;
        this.radialMenuController = radialMenuController;
        this.zoomController = zoomController;
        this.panController = panController;
        this.keyboardController = keyboardController;
        this.selectionRectangleController = selectionRectangleController;
        this.blockSearchController = blockSearchController;
        this.view = editorView;

        // Used for pasting TODO refactor and remove
        mousePositionOnScene = new SimpleObjectProperty(new Point2D(0, 0));

        view.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 
        view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_STARTED, scrollStartedHandler);
        view.addEventHandler(ScrollEvent.SCROLL, scrollHandler);
        view.addEventHandler(ScrollEvent.SCROLL_FINISHED, scrollFinishedHandler);
        view.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedHandler);
    }

    private void handleMouseMoved(MouseEvent event) {
        mousePositionOnScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
    }

    private void handleMouseClicked(MouseEvent event) {
        radialMenuController.processEditorMouseClicked(event);
        blockSearchController.processEditorMouseClicked(event);
    }

    private void handleMousePressed(MouseEvent event) {
        mousePositionOnScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
        panController.processEditorPanStarted(event);
        selectionRectangleController.processEditorSelectionStarted(event);
    }

    private void handleMouseDragged(MouseEvent event) {
        panController.processEditorPan(event);
        selectionRectangleController.processEditorSelection(event);
    }

    private void handleMouseReleased(MouseEvent event) {
        panController.processEditorPanFinished(event);
        selectionRectangleController.processEditorSelectionFinished(event);
    }

    private void handleScrollStarted(ScrollEvent event) {
        zoomController.processEditorScrollStarted(event);
    }

    private void handleScroll(ScrollEvent event) {
        zoomController.processEditorScroll(event);
    }

    private void handleScrollFinished(ScrollEvent event) {
        zoomController.processEditorScrollFinished(event);
    }

    private void handleKeyPressed(KeyEvent event) {
        keyboardController.processEditorShortcutAction(event);
    }

}

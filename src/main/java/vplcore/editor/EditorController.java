package vplcore.editor;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import vplcore.editor.radialmenu.RadialMenuController;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.BlockSearchController;

/**
 *
 * @author Joost
 */
public class EditorController {

    private final Workspace workspace;
    private final RadialMenuController radialMenuController;
    private final ZoomController zoomController;
    private final PanController panController;
    private final KeyboardController keyboardController;
    private final SelectionRectangleController selectionRectangleController;
    private final BlockSearchController blockSearchController;
    private final EditorView view;

    private final ObjectProperty<Point2D> mousePositionOnScene;

    private final EventHandler<MouseEvent> mouseMovedHandler;
    private final EventHandler<MouseEvent> mouseClickedHandler;
    private final EventHandler<MouseEvent> mousePressedHandler;
    private final EventHandler<MouseEvent> mouseDraggedHandler;
    private final EventHandler<MouseEvent> mouseReleasedHandler;
    private final EventHandler<ScrollEvent> scrollHandler;
    private final EventHandler<KeyEvent> keyPressedHandler;

    public EditorController(EditorView editorView, RadialMenuController radialMenuController, Workspace workspace, ZoomController zoomController, PanController panController, KeyboardController keyboardController, SelectionRectangleController selectionRectangleController, BlockSearchController blockSearchController) {
        this.workspace = workspace;
        this.radialMenuController = radialMenuController;
        this.zoomController = zoomController;
        this.panController = panController;
        this.keyboardController = keyboardController;
        this.selectionRectangleController = selectionRectangleController;
        this.blockSearchController = blockSearchController;
        this.view = editorView;

        // Used for pasting and positioning the SelectBlock TODO refactor and remove
        mousePositionOnScene = new SimpleObjectProperty(new Point2D(0, 0));

        mouseMovedHandler = this::handleMouseMoved;
        mouseClickedHandler = this::handleMouseClicked;
        mousePressedHandler = this::handleMousePressed;
        mouseDraggedHandler = this::handleMouseDragged;
        mouseReleasedHandler = this::handleMouseReleased;
        scrollHandler = this::handleScroll;
        keyPressedHandler = this::handleKeyPressed;

        view.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 
        view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        view.addEventHandler(ScrollEvent.SCROLL, scrollHandler);
        view.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
    }

    private void handleMouseMoved(MouseEvent event) {
        mousePositionOnScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
    }

    private void handleMouseClicked(MouseEvent event) {
        radialMenuController.handleEditorMouseClicked(event);
        blockSearchController.handleEditorMouseClicked(event);
    }

    private void handleMousePressed(MouseEvent event) {
        mousePositionOnScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
        panController.handleEditorPanStarted(event);
        selectionRectangleController.handleEditorSelectionStarted(event);
    }

    private void handleMouseDragged(MouseEvent event) {
        panController.handleEditorPan(event);
        selectionRectangleController.handleEditorSelection(event);
    }

    private void handleMouseReleased(MouseEvent event) {
        panController.handleEditorPanStopped(event);
        selectionRectangleController.handleEditorSelectionStopped(event);
    }

    private void handleScroll(ScrollEvent event) {
        zoomController.handleEditorScroll(event);
    }

    private void handleKeyPressed(KeyEvent event) {
        keyboardController.handleKeyPressed(event);
    }

}

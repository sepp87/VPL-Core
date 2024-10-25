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
    private final EditorView view;

    private final ObjectProperty<Point2D> mousePositionOnScene;

    private final EventHandler<MouseEvent> mouseMovedHandler;
    private final EventHandler<MouseEvent> mouseClickedHandler;
    private final EventHandler<MouseEvent> mousePressedHandler;
    private final EventHandler<MouseEvent> mouseDraggedHandler;
    private final EventHandler<MouseEvent> mouseReleasedHandler;
    private final EventHandler<ScrollEvent> scrollHandler;
    private final EventHandler<KeyEvent> keyPressedHandler;

    public EditorController(EditorView editorView, RadialMenuController radialMenuController, Workspace workspace, ZoomController zoomController, PanController panController, KeyboardController keyboardController, SelectionRectangleController selectionRectangleController) {
        this.workspace = workspace;
        this.radialMenuController = radialMenuController;
        this.zoomController = zoomController;
        this.panController = panController;
        this.keyboardController = keyboardController;
        this.selectionRectangleController = selectionRectangleController;
        this.view = editorView;

        // Used for pasting and positioning the SelectBlock TODO refactor and remove
        this.mousePositionOnScene = new SimpleObjectProperty(new Point2D(0, 0));

        this.mouseMovedHandler = this::handleMouseMoved;
        this.mouseClickedHandler = this::handleMouseClicked;
        this.mousePressedHandler = this::handleMousePressed;
        this.mouseDraggedHandler = this::handleMouseDragged;
        this.mouseReleasedHandler = this::handleMouseReleased;
        this.scrollHandler = this::handleScroll;
        this.keyPressedHandler = this::handleKeyPressed;

        this.view.addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        this.view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 
        this.view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        this.view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        this.view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        this.view.addEventHandler(ScrollEvent.SCROLL, scrollHandler);
        this.view.addEventFilter(KeyEvent.KEY_PRESSED, keyPressedHandler);
    }

    private void handleMouseMoved(MouseEvent event) {
        mousePositionOnScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
    }

    private void handleMouseClicked(MouseEvent event) {
        radialMenuController.handleMouseClicked(event);
    }

    private void handleMousePressed(MouseEvent event) {
        mousePositionOnScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
        panController.handleMousePressed(event);
        selectionRectangleController.handleMousePressed(event);
    }

    private void handleMouseDragged(MouseEvent event) {
        panController.handleMouseDragged(event);
        selectionRectangleController.handleMouseDragged(event);
    }

    private void handleMouseReleased(MouseEvent event) {
        panController.handleMouseReleased(event);
        selectionRectangleController.handleMouseReleased(event);
    }

    private void handleScroll(ScrollEvent event) {
        zoomController.handleScroll(event);
    }

    private void handleKeyPressed(KeyEvent event) {
        keyboardController.handleKeyPressed(event);
    }

}

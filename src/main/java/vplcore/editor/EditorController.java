package vplcore.editor;

import vplcore.editor.radialmenu.RadialMenuController;
import javafx.event.EventHandler;
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

    private final EditorView view;

    private final EventHandler<MouseEvent> mouseClickedHandler;
    private final EventHandler<MouseEvent> mousePressedHandler;
    private final EventHandler<MouseEvent> mouseDraggedHandler;
    private final EventHandler<MouseEvent> mouseReleasedHandler;
    private final EventHandler<ScrollEvent> scrollHandler;

    public EditorController(EditorView editorView, RadialMenuController radialMenuController, Workspace workspace, ZoomController zoomController, PanController panController) {
        this.workspace = workspace;
        this.radialMenuController = radialMenuController;
        this.zoomController = zoomController;
        this.panController = panController;
        this.view = editorView;

        this.scrollHandler = this::handleScroll;
        this.view.addEventHandler(ScrollEvent.SCROLL, scrollHandler);

        this.mouseClickedHandler = this::handleMouseClicked;
        this.view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 

        this.mousePressedHandler = this::handleMousePressed;
        this.view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);

        this.mouseDraggedHandler = this::handleMouseDragged;
        this.view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);

        this.mouseReleasedHandler = this::handleMouseReleased;
        this.view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
    }

    private void handleScroll(ScrollEvent event) {
//        zoomController.handleScroll(event);
    }

    private void handleMouseClicked(MouseEvent event) {
        radialMenuController.handleMouseClicked(event);
    }

    private void handleMousePressed(MouseEvent event) {
//        panController.handleMousePressed(event);
    }

    private void handleMouseDragged(MouseEvent event) {
//        panController.handleMouseDragged(event);
    }

    private void handleMouseReleased(MouseEvent event) {
//        panController.handleMouseReleased(event);
    }

}

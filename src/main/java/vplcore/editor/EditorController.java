package vplcore.editor;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import vplcore.App;
import vplcore.context.EventRouter;

/**
 *
 * @author Joost
 */
public class EditorController extends BaseController {

    private final EventRouter eventRouter;

    private final EditorView view;

    private final ChangeListener<Object> requestFocusHandler = this::requestFocus;
    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;
    private final EventHandler<MouseEvent> mousePressedHandler = this::handleMousePressed;
    private final EventHandler<MouseEvent> mouseDraggedHandler = this::handleMouseDragged;
    private final EventHandler<MouseEvent> mouseReleasedHandler = this::handleMouseReleased;
    private final EventHandler<ScrollEvent> scrollStartedHandler = this::handleScrollStarted;
    private final EventHandler<ScrollEvent> scrollUpdatedHandler = this::handleScroll;
    private final EventHandler<ScrollEvent> scrollFinishedHandler = this::handleScrollFinished;
    private final EventHandler<KeyEvent> keyPressedHandler = this::handleKeyPressed;

    public EditorController(String contextId, EditorView editorView) {

        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.view = editorView;

        view.sceneProperty().addListener(requestFocusHandler); // request focus immediately after scene is shown, else KeyEvent SPACE is not handled
        view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 
        view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_STARTED, scrollStartedHandler);
        view.addEventHandler(ScrollEvent.SCROLL, scrollUpdatedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_FINISHED, scrollFinishedHandler);
        view.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedHandler);

    }

    private void requestFocus(Object b, Object o, Object n) {
        view.requestFocus();
    }

    private void handleMouseClicked(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleMousePressed(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleMouseDragged(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleMouseReleased(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleScrollStarted(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleScroll(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleScrollFinished(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private void handleKeyPressed(KeyEvent event) {
        eventRouter.fireEvent(event);
    }
}

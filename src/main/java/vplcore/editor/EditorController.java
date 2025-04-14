package vplcore.editor;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import vplcore.App;
import vplcore.context.EventRouter;
import static vplcore.util.EditorUtils.onFreeSpace;

/**
 *
 * @author Joost
 */
public class EditorController extends BaseController {

    private final EventRouter eventRouter;

    private final EditorView view;

    public EditorController(String contextId, EditorView editorView) {

        super(contextId);
        this.eventRouter = App.getContext(contextId).getEventRouter();
        this.view = editorView;

        view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 
        view.addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        view.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        view.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_STARTED, scrollStartedHandler);
        view.addEventHandler(ScrollEvent.SCROLL, scrollUpdatedHandler);
        view.addEventHandler(ScrollEvent.SCROLL_FINISHED, scrollFinishedHandler);
    }

    public void initialize(Scene scene) {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyPressedHandler);
    }

    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;

    private void handleMouseClicked(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<MouseEvent> mousePressedHandler = this::handleMousePressed;

    private void handleMousePressed(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<MouseEvent> mouseDraggedHandler = this::handleMouseDragged;

    private void handleMouseDragged(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<MouseEvent> mouseReleasedHandler = this::handleMouseReleased;

    private void handleMouseReleased(MouseEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<ScrollEvent> scrollStartedHandler = this::handleScrollStarted;

    private void handleScrollStarted(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<ScrollEvent> scrollUpdatedHandler = this::handleScroll;

    private void handleScroll(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<ScrollEvent> scrollFinishedHandler = this::handleScrollFinished;

    private void handleScrollFinished(ScrollEvent event) {
        eventRouter.fireEvent(event);
    }

    private final EventHandler<KeyEvent> keyPressedHandler = this::handleKeyPressed;

    private void handleKeyPressed(KeyEvent event) {
        eventRouter.fireEvent(event);
    }

}

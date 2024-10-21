package vplcore.editor;

import vplcore.editor.radialmenu.RadialMenuController;
import vplcore.editor.EditorView;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;
import vplcore.editor.radialmenu.RadialMenu;
import vplcore.workspace.input.ZoomController;

/**
 *
 * @author Joost
 */
public class EditorController {

    private final Workspace workspace;
    private final RadialMenuController radialMenuController;
    private final ZoomController zoomController;
    
    private final EditorView view;

    private final EventHandler<MouseEvent> mouseClickedHandler;
    private final EventHandler<ScrollEvent> scrollHandler;

    public EditorController(EditorView editorView, RadialMenuController radialMenuController, Workspace workspace, ZoomController zoomController) {
        this.workspace = workspace;
        this.radialMenuController = radialMenuController;
        this.zoomController = zoomController;
        this.view = editorView;

        this.mouseClickedHandler = this::handleMouseClicked;
        this.view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 

        this.scrollHandler = this::handleScroll;
        this.view.addEventFilter(ScrollEvent.SCROLL, scrollHandler);
    }
    
    private void handleScroll(ScrollEvent event) {
        zoomController.handleScroll(event);
    }

    private void handleMouseClicked(MouseEvent event) {
        radialMenuController.handleMouseClicked(event);
    }

    public void showRadialMenu(double x, double y) {
        radialMenuController.showRadialMenu(x, y);
    }

    public void hideRadialMenu() {
        radialMenuController.hideRadialMenu();
    }
}

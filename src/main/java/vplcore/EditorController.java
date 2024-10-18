package vplcore;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.workspace.RadialMenuController;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.radialmenu.RadialMenu;

/**
 *
 * @author Joost
 */
public class EditorController {

    private final Workspace workspace;
    private final RadialMenuController radialMenuController;
    private final EditorView view;

    private final EventHandler<MouseEvent> mouseClickedHandler;

    public EditorController(EditorView editorView, RadialMenuController radialMenuController, Workspace workspace) {
        this.workspace = workspace;
        this.radialMenuController = radialMenuController;
        this.view = editorView;

        this.mouseClickedHandler = this::handleMouseClicked;
        this.view.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler); // capture the event before the sub menu is removed from the radial menu when clicking on "Return To Main" from a sub menu 

    }

    private void handleMouseClicked(MouseEvent event) {

        Node intersectedNode = event.getPickResult().getIntersectedNode();
        boolean onEditorOrWorkspace = intersectedNode instanceof EditorView || intersectedNode instanceof Workspace;
        boolean onRadialMenu = Workspace.checkParents(intersectedNode, RadialMenu.class);
        boolean isSecondaryClick = event.getButton() == MouseButton.SECONDARY && event.isStillSincePress();
        boolean mouseIsIdle = workspace.getMouseMode() == MouseMode.MOUSE_IDLE;

        // keeping the following until 3D viewer is implemented
//            System.out.println(node.getClass().getSimpleName());
//            boolean onControl = workspace.checkParent(node, Control.class);
//            boolean onViewer = workspace.checkParent(node, Shape3D.class);

        if (isSecondaryClick && onEditorOrWorkspace && mouseIsIdle) {
            showRadialMenu(event.getSceneX(), event.getSceneY());
        } else if (onRadialMenu) {
            // keep radial menu shown if it is clicked on
        } else {
            hideRadialMenu();
        }
    }

    public void showRadialMenu(double x, double y) {
        radialMenuController.showRadialMenu(x, y);
    }

    public void hideRadialMenu() {
        radialMenuController.hideRadialMenu();
    }
}

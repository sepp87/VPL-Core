package vplcore.editor;

import vplcore.editor.radialmenu.RadialMenuView;
import javafx.scene.layout.AnchorPane;
import vplcore.Config;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author Joost
 */
public class EditorView extends AnchorPane {

    MenuBarView menuBarView;

    public EditorView(RadialMenuView radialMenuView, WorkspaceView workspaceView, MenuBarView menuBarView, ZoomView zoomView, SelectionRectangleView selectionRectangleView, BlockSearchView blockSearchView) {

        this.getStylesheets().add(Config.get().stylesheets());
        this.getStyleClass().add("vpl");

        this.menuBarView = menuBarView;
        
        menuBarView.prefWidthProperty().bind(this.widthProperty());

        AnchorPane.setTopAnchor(zoomView, 37.5);
        AnchorPane.setRightAnchor(zoomView, 10.);

        // create selection block
        this.getChildren().addAll(workspaceView, radialMenuView, menuBarView, zoomView, selectionRectangleView, blockSearchView);

    }

    public void printMenuBarHeight() {
        System.out.println("EditorView.printMenuBarHeight() " + menuBarView.getHeight());
    }

}

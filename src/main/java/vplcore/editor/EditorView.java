package vplcore.editor;

import vplcore.editor.radialmenu.RadialMenuView;
import javafx.scene.layout.AnchorPane;
import vplcore.Config;
import vplcore.workspace.Workspace;

/**
 *
 * @author Joost
 */
public class EditorView extends AnchorPane {

    Workspace workspace;
    MenuBarView menuBarView;

    public EditorView(RadialMenuView radialMenuView, Workspace workspace, MenuBarView menuBarView, ZoomView zoomView, SelectionRectangleView selectionRectangleView, BlockSearchView blockSearchView) {

        this.getStylesheets().add(Config.get().stylesheets());
        this.getStyleClass().add("vpl");

        this.workspace = workspace;
        this.menuBarView = menuBarView;
        
        menuBarView.prefWidthProperty().bind(this.widthProperty());

        AnchorPane.setTopAnchor(zoomView, 37.5);
        AnchorPane.setRightAnchor(zoomView, 10.);

        // create selection block
        this.getChildren().addAll(workspace, radialMenuView, menuBarView, zoomView, selectionRectangleView, blockSearchView);

    }

    public void test() {
        System.out.println("MenuBar Height " + menuBarView.getHeight());
    }

}

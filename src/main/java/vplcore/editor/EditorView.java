package vplcore.editor;

import vplcore.editor.radialmenu.RadialMenuView;
import java.io.File;
import javafx.scene.layout.AnchorPane;
import vplcore.Config;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.SelectBlockHandler;

/**
 *
 * @author Joost
 */
public class EditorView extends AnchorPane {

    Workspace workspace;
    MenuBarView menuBarView;

    public EditorView(RadialMenuView radialMenuView, Workspace workspace, MenuBarView menuBarView, ZoomView zoomView, SelectionRectangleView selectionRectangleView) {

        this.getStylesheets().add(Config.get().stylesheets());
        this.getStyleClass().add("vpl");

        this.workspace = workspace;
        this.menuBarView = menuBarView;

        menuBarView.prefWidthProperty().bind(this.widthProperty());

        AnchorPane.setTopAnchor(zoomView, 37.5);
        AnchorPane.setRightAnchor(zoomView, 10.);

        // create selection block
        SelectBlock selectBlock = new SelectBlockHandler(workspace).getSelectBlock();
        this.getChildren().addAll(workspace, radialMenuView, menuBarView, zoomView, selectBlock, selectionRectangleView);


    }

    public void test() {
        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);
        System.out.println("MenuBar Height " + menuBarView.getHeight());
    }

}

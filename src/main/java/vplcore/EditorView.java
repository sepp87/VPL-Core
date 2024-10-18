package vplcore;

import java.io.File;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.MenuBarView;
import vplcore.workspace.RadialMenuView;
import vplcore.workspace.Workspace;
import vplcore.workspace.input.SelectBlockHandler;
import vplcore.workspace.input.ZoomManager;

/**
 *
 * @author Joost
 */
public class EditorView extends AnchorPane {

    
    Workspace workspace;
    MenuBar menuBar;
    
    public EditorView(RadialMenuView radialMenuView, Workspace workspace) {

        this.getStylesheets().add(Config.get().stylesheets());
        this.getStyleClass().add("vpl");

        this.workspace = workspace;

        // TODO create MenuBarView
        // TODO create MenuBarController
        this.menuBar = new MenuBarView(workspace).configure();
        menuBar.prefWidthProperty().bind(this.widthProperty());

        // TODO create ZoomControlsView
        // TODO create ZoomController
        ZoomManager zoomControls = new ZoomManager(workspace);
        AnchorPane.setTopAnchor(zoomControls, 37.5);
        AnchorPane.setRightAnchor(zoomControls, 10.);

        // create selection block
        SelectBlock selectBlock = new SelectBlockHandler(workspace).getSelectBlock();
        this.getChildren().addAll(workspace, radialMenuView, menuBar, zoomControls, selectBlock);
//        this.getChildren().addAll(workspace, radialMenu, menuBar, zoomControls, selectBlock);

    }

    public void test() {
        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);
        System.out.println("MenuBar Height " + menuBar.getHeight());
    }

}

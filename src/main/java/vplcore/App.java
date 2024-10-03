package vplcore;

import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.model.ResizeButton;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.Workspace;
import vplcore.workspace.MenuBarConfigurator;
import vplcore.workspace.input.SelectBlockHandler;
import vplcore.workspace.input.ZoomManager;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        AnchorPane pane = new AnchorPane();
        pane.getStylesheets().add(Config.get().stylesheets());
        pane.getStyleClass().add("vpl");
//        pane.setStyle("-fx-background-color: blue;");

        Workspace workspace = new Workspace();
        // get selection hub
        // get radial menu
        Group workspaceAndRadialMenu = workspace.Go();

        // create menu bar
        MenuBar menuBar = new MenuBarConfigurator(workspace).configure();
        menuBar.prefWidthProperty().bind(pane.widthProperty());

        // create zoom controls
        ZoomManager zoomControls = new ZoomManager(workspace);
        AnchorPane.setTopAnchor(zoomControls, 37.5);
        AnchorPane.setRightAnchor(zoomControls, 10.);

        // create selection block
        SelectBlock selectBlock = new SelectBlockHandler(workspace).getSelectBlock();

        pane.getChildren().addAll(workspaceAndRadialMenu, menuBar, zoomControls, selectBlock);

        Scene scene = new Scene(pane, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
        stage.setFullScreen(false);

//        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);
//        System.out.println("MenuBar Height " + menuBar.getHeight());

    }

}

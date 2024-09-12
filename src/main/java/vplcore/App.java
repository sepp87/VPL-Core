package vplcore;

import java.io.File;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vplcore.graph.io.GraphLoader;
import vplcore.workspace.Workspace;
import vplcore.workspace.MenuBarConfigurator;
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
        pane.setStyle("-fx-background-color: blue;");

        Workspace workspace = new Workspace();
        // get selection hub
        // get radial menu
        Group workspaceAndRadialMenu = workspace.Go();

        MenuBar menuBar = new MenuBarConfigurator(workspace).configure();
        menuBar.prefWidthProperty().bind(pane.widthProperty());

        ZoomManager zoomControls = new ZoomManager(workspace);
        AnchorPane.setTopAnchor(zoomControls, 37.5);
        AnchorPane.setRightAnchor(zoomControls, 10.);

        pane.getChildren().addAll(workspaceAndRadialMenu, menuBar, zoomControls);
//        pane.getChildren().addAll(workspaceAndRadialMenu, menuBar);

        Scene scene = new Scene(pane, 800, 800);
        stage.setScene(scene);
        stage.setTitle("VPLTester");
        stage.show();
        stage.setFullScreen(false);

        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);

        System.out.println(menuBar.getHeight());

    }

}

package vplcore;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vplcore.workspace.Workspace;
import vplcore.workspace.MenuBarConfigurator;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        AnchorPane pane = new AnchorPane();
        pane.getStylesheets().add("css/flat_white.css");
//        pane.getStylesheets().add("css/flat_dark.css");
//        pane.getStylesheets().add("css/flat_singer.css");
        pane.getStyleClass().add("vpl");

        Workspace workspace = new Workspace();
        // get selection hub
        // get radial menu
        Group workspaceAndRadialMenu = workspace.Go();

        MenuBar menuBar = new MenuBarConfigurator(workspace).configure();
        menuBar.prefWidthProperty().bind(pane.widthProperty());

        pane.getChildren().addAll(workspaceAndRadialMenu, menuBar);

        Scene scene = new Scene(pane, 1600, 1200);
        stage.setScene(scene);
        stage.setTitle("VPLTester");
        stage.show();
        stage.setFullScreen(false);

    }

}

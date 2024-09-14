package vplcore;

import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.model.BlockException;
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
        pane.setStyle("-fx-background-color: blue;");

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

        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);
        System.out.println("MenuBar Height " + menuBar.getHeight());

//        testException(pane, workspace);
    }

    public static void testException(AnchorPane pane, Workspace workspace) {

        AnchorPane p = new AnchorPane();
        p.setMaxHeight(0);
        p.setMaxWidth(0);
        p.setMinHeight(0);
        p.setMinWidth(0);
        p.setLayoutX(300);
        p.setLayoutY(300);
        p.setStyle("-fx-background-color: red;");
        BlockException e = new BlockException();
        e.setExceptions(List.of("This is a mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.", "Short message! 🧐", "This is the second mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows."));

        e.maximize();
        p.getChildren().add(e);

        AnchorPane.setBottomAnchor(e, 10.0);  // Align the whole panel to the bottom of its container
        AnchorPane.setLeftAnchor(e, 10.0);

        workspace.getChildren().add(p);

//        pane.getChildren().add(p);
    }

}

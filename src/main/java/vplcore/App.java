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
import vplcore.graph.model.BlockInfoPanel;
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

//        BlockInfoPanel infoPanel = new BlockInfoPanel(workspace);
//        workspace.getChildren().add(infoPanel);
//        infoPanel.setMessages(List.of(
//                "Short message! 🧐",
//                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus eget odio vel purus sodales ullamcorper. Sed id suscipit ante, vitae molestie quam. Donec turpis nulla, rhoncus ac fermentum sit amet, tempus non justo. Proin mattis fringilla dui. Curabitur elementum, odio ut porta rhoncus, quam sapien fermentum augue, vitae mattis risus velit quis mauris. Nam eleifend tortor ac dignissim aliquam. In bibendum magna sed erat ultricies, id imperdiet odio ultrices. Etiam in euismod nunc. Nullam varius lacus eu est aliquet tempus. Fusce suscipit, enim vel maximus tristique, erat mauris hendrerit quam, ac convallis augue dui id nulla. Praesent convallis diam non nunc cursus feugiat. Nullam gravida, tortor a bibendum iaculis, erat mauris dapibus lacus, eu lobortis turpis enim luctus quam. Morbi sed lectus suscipit nibh lacinia viverra. Fusce laoreet tortor at risus molestie ultrices.\n"
//                + "\n"
//                + "Vivamus pellentesque eros mi, nec commodo leo sagittis mollis. Suspendisse ultricies ac nisi id facilisis. Sed ac nisl quis neque blandit vestibulum. Nunc ullamcorper odio at ante tincidunt ultrices. Aliquam nec varius sem. Donec sed convallis nibh. Donec nec ultricies tellus, at pulvinar tortor. Nullam enim dolor, malesuada sit amet libero euismod, imperdiet faucibus elit. Ut ligula dui, luctus vel venenatis at, vehicula in metus. Nunc ultricies id nunc sit amet dignissim. Maecenas et nunc lacus. Donec sit amet sapien hendrerit turpis interdum vulputate a vitae metus.\n"
//                + "\n"
//                + "Praesent non tincidunt orci. Morbi egestas ex velit, eget laoreet ipsum posuere et. Morbi tempor lacinia tincidunt. Mauris vitae arcu sed neque aliquam malesuada. Suspendisse a efficitur mi, ac vestibulum elit. Donec luctus gravida dui vel mollis. Ut gravida urna lorem, sed tincidunt elit pellentesque sed. Mauris viverra pharetra purus, nec ultricies enim rhoncus dictum. Ut odio purus, scelerisque quis arcu sed, ullamcorper tincidunt risus. Praesent ac velit ut nibh rutrum malesuada id non nulla.",
//                "This is a mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.",
//                "This is the second mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows."));

        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);
        System.out.println("MenuBar Height " + menuBar.getHeight());


    }

    
    


}

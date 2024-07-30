package jo.vpl;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jo.vpl.core.VplControl;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        System.out.println(Thread.currentThread().getName());

        AnchorPane pane = new AnchorPane();

        VplControl host = new VplControl();
        Group vplContent = host.Go();

        MenuBar menuBar = getMenuBar();
        menuBar.prefWidthProperty().bind(pane.widthProperty());

        pane.getChildren().addAll(vplContent, menuBar);
        pane.getStylesheets().add("css/flat_white.css");
//        pane.getStylesheets().add("css/flat_dark.css");
//        pane.getStylesheets().add("css/default.css");
        pane.getStyleClass().add("vpl");

        Scene scene = new Scene(pane, 1600, 1200);

        stage.setScene(scene);
        stage.setTitle("VPLTester");
        stage.show();

        stage.setFullScreen(false);

    }

    public MenuBar getMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        Menu file = new Menu("File");
        MenuItem newFile = new MenuItem("New file");
        MenuItem openFile = new MenuItem("Open file");
        MenuItem save = new MenuItem("Save");
        file.getItems().addAll(newFile, openFile, save);

        Menu edit = new Menu("Edit");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");
        MenuItem delete = new MenuItem("Delete");
        Menu align = new Menu("Align");
        edit.getItems().addAll(copy, paste, delete, align);

        MenuItem alignLeft = new MenuItem("Align left");
        MenuItem alignVertically = new MenuItem("Align vertically");
        MenuItem alignRight = new MenuItem("Align right");
        MenuItem alignTop = new MenuItem("Align top");
        MenuItem alignHorizontally = new MenuItem("Align horizontally");
        MenuItem alignBottom = new MenuItem("Align bottom");
        align.getItems().addAll(alignLeft, alignVertically, alignRight, alignTop, alignHorizontally, alignBottom);

        Menu view = new Menu("View");
        MenuItem zoomToFit = new MenuItem("Zoom to fit");
        MenuItem zoomIn = new MenuItem("Zoom in");
        MenuItem zoomOut = new MenuItem("Zoom out");
        view.getItems().addAll(zoomToFit, zoomIn, zoomOut);

        menuBar.getMenus().addAll(file, edit, view);
        return menuBar;
    }

}

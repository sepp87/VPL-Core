package vplcore;

import java.io.File;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import vplcore.graph.io.GraphLoader;
import vplcore.graph.model.BlockException;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.DraggablePanel;
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

//        workspace.getChildren().add(testInfoPanel());

        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace);
        System.out.println("MenuBar Height " + menuBar.getHeight());

    }

    public static Group testInfoPanel() {
        Group container = new Group();
        container.setStyle("-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.1) , 10,0,7,7 );");

        double height = 220;

        VBox infoBubble = getInfoBubble();
        infoBubble.setStyle("-fx-background-color: #5F5F5F;-fx-background-radius: 4;");
        infoBubble.setPrefWidth(220);
        infoBubble.setPrefHeight(height);
        infoBubble.setLayoutY(-height);

        // 24,3 > 220
        // 4,4 * 9,05349794239 = ±40
        // 4 * 9,05349794239 = ±36,2
        // 2,6 * 9,05349794239 = ±23,53
        Path triangle = getTail();

        container.getChildren().addAll(infoBubble, triangle);
        return container;
    }

    public static VBox getInfoBubble() {
        VBox infoBubble = new VBox();

        Button closeButton = new Button("X");
        Label infoText = new Label();
        infoText.setWrapText(true);

        infoBubble.getChildren().addAll(closeButton, infoText);
        return infoBubble;
    }


    public static Path getTail() {

        double radius = 13.5;
        double width = 125 - radius * 2;
        double height = 95 - radius;
        double factor = width / 36.2;
        double strokeWidth = radius * 2 / factor;
        double offsetX = 40 + strokeWidth;

        Point2D a = new Point2D(offsetX + width / factor + 2, -2); // add and substract two pixels to mitigate the seam between the infoBubble and the tail
        Point2D b = new Point2D(offsetX, height / factor);
        Point2D c = new Point2D(offsetX, -2);

        Path triangle = new Path();
        triangle.getElements().add(new MoveTo(a.getX(), a.getY()));
        triangle.getElements().add(new LineTo(b.getX(), b.getY()));
        triangle.getElements().add(new LineTo(c.getX(), c.getY()));
        triangle.setStyle("-fx-fill: #5F5F5F;-fx-stroke: #5F5F5F;-fx-stroke-width: " + strokeWidth + "px;-fx-stroke-line-join: round;");

        return triangle;
    }

    public static Path getRoundedTriangle() {

        double radius = 10;
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(100, 0);
        Point2D c = new Point2D(0, 100);

        Path triangle = new Path();
//        triangle.getElements().add(new MoveTo(x1, y1 + radius));
//        triangle.getElements().add(new LineTo(x1 - radius, y1 + radius));
//        triangle.getElements().add(new ArcTo(radius, radius, 0, x2 + radius, y2, false, true));
//        triangle.getElements().add(new LineTo(x2 + radius, y2));
//        triangle.getElements().add(new ArcTo(radius, radius, 0, x3 - radius, y3, false, true));
//        triangle.getElements().add(new LineTo(x3 - radius, y3));
//        triangle.getElements().add(new ArcTo(radius, radius, 0, x1, y1 + radius, false, true));
//        triangle.getElements().add(new ClosePath());
        return triangle;
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

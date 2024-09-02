package vplcore;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
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
        pane.getStylesheets().add(Config.get().stylesheets());
        pane.getStyleClass().add("vpl");
        pane.setStyle("-fx-background-color: blue;");

        Workspace workspace = new Workspace();
        // get selection hub
        // get radial menu
        Group workspaceAndRadialMenu = workspace.Go();

        MenuBar menuBar = new MenuBarConfigurator(workspace).configure();
        menuBar.prefWidthProperty().bind(pane.widthProperty());

        pane.getChildren().addAll(workspaceAndRadialMenu, menuBar);


        Scene scene = new Scene(pane, 800, 1200);
        stage.setScene(scene);
        stage.setTitle("VPLTester");
        stage.show();
        stage.setFullScreen(false);

    }

    
    
//    private static final double SNAP_POINT_RADIUS = 10.0; // Radius of the red circle
//    private static final double INVISIBLE_CURVE_WIDTH = 40.0; // Width of the invisible curve for detection
//
//    @Override
//    public void start(Stage primaryStage) {
//        Pane pane = new Pane();
//
//        // Create the visible CubicCurve
//        CubicCurve visibleCurve = new CubicCurve();
//        visibleCurve.setStartX(100);
//        visibleCurve.setStartY(150);
//        visibleCurve.setControlX1(150);
//        visibleCurve.setControlY1(50);
//        visibleCurve.setControlX2(250);
//        visibleCurve.setControlY2(250);
//        visibleCurve.setEndX(300);
//        visibleCurve.setEndY(150);
//        visibleCurve.setStroke(Color.BLACK);
//        visibleCurve.setStrokeWidth(2);
//        visibleCurve.setFill(null);
//
//        // Create an invisible curve for snapping detection
//        CubicCurve snappingCurve = new CubicCurve();
//        snappingCurve.setStartX(visibleCurve.getStartX());
//        snappingCurve.setStartY(visibleCurve.getStartY());
//        snappingCurve.setControlX1(visibleCurve.getControlX1());
//        snappingCurve.setControlY1(visibleCurve.getControlY1());
//        snappingCurve.setControlX2(visibleCurve.getControlX2());
//        snappingCurve.setControlY2(visibleCurve.getControlY2());
//        snappingCurve.setEndX(visibleCurve.getEndX());
//        snappingCurve.setEndY(visibleCurve.getEndY());
//        snappingCurve.setStrokeWidth(INVISIBLE_CURVE_WIDTH); // Make it wider for easier snapping
//        snappingCurve.setStroke(Color.TRANSPARENT); // Invisible stroke
//        snappingCurve.setFill(null);
//
//        // Create a red circle that will appear when snapping
//        Circle snapPoint = new Circle(SNAP_POINT_RADIUS, Color.RED);
//        snapPoint.setVisible(false); // Hide it initially
//
//        pane.getChildren().addAll(visibleCurve, snappingCurve, snapPoint);
//
//        // Add mouse moved listener to update the snap point position along the visible curve
//        snappingCurve.setOnMouseMoved(event -> handleMouseMoved(event, visibleCurve, snapPoint));
//
//        // Show an alert when the snap point is clicked
//        snapPoint.setOnMouseClicked(event -> showSnapPointClickedAlert());
//
//        Scene scene = new Scene(pane, 400, 400);
//        primaryStage.setTitle("Snapping with Invisible Curve Example");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }
//
//    private void handleMouseMoved(MouseEvent event, CubicCurve visibleCurve, Circle snapPoint) {
//        double mouseX = event.getX();
//        double mouseY = event.getY();
//
//        // Check proximity to the curve using parameter t
//        double closestDistance = Double.MAX_VALUE;
//        double closestX = 0;
//        double closestY = 0;
//
//        for (double t = 0; t <= 1.0; t += 0.01) {
//            double curveX = cubicCurvePoint(visibleCurve.getStartX(), visibleCurve.getControlX1(), visibleCurve.getControlX2(), visibleCurve.getEndX(), t);
//            double curveY = cubicCurvePoint(visibleCurve.getStartY(), visibleCurve.getControlY1(), visibleCurve.getControlY2(), visibleCurve.getEndY(), t);
//
//            double distance = Math.sqrt(Math.pow(mouseX - curveX, 2) + Math.pow(mouseY - curveY, 2));
//
//            if (distance < closestDistance) {
//                closestDistance = distance;
//                closestX = curveX;
//                closestY = curveY;
//            }
//        }
//
//        // Update the snap point position at the closest point on the visible curve
//        snapPoint.setCenterX(closestX);
//        snapPoint.setCenterY(closestY);
//
//        // If the closest distance is within 20 pixels, show the snap point
//        if (closestDistance <= 20) {
//            snapPoint.setVisible(true);
//        } else {
//            snapPoint.setVisible(false);
//        }
//    }
//
//    // Helper method to calculate cubic Bezier point
//    private double cubicCurvePoint(double start, double control1, double control2, double end, double t) {
//        double u = 1 - t;
//        return Math.pow(u, 3) * start + 3 * Math.pow(u, 2) * t * control1 + 3 * u * Math.pow(t, 2) * control2 + Math.pow(t, 3) * end;
//    }
//
//    // Show an alert when the snap point is clicked
//    private void showSnapPointClickedAlert() {
//        Alert alert = new Alert(Alert.AlertType.INFORMATION);
//        alert.setTitle("Snap Point Clicked");
//        alert.setHeaderText(null);
//        alert.setContentText("You clicked on the snap point!");
//        alert.showAndWait();
//    }


}

package jo.vpl;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import jo.vpl.core.VPLControl;

public class VPLTester extends Application {

    int snapshotCounter = 0;

    public void start(Stage stage) throws Exception {

        AnchorPane pane = new AnchorPane();

        Group vplContent = new VPLControl().Go();

        pane.getChildren().addAll(vplContent);
        pane.getStylesheets().add("css/flat_white.css");
//        pane.getStylesheets().add("css/flat_dark.css");
//        pane.getStylesheets().add("css/default.css");
        pane.getStyleClass().add("vpl");

        Scene scene = new Scene(pane, 800, 600);

        stage.setScene(scene);
        stage.setTitle("VPLTester");
        stage.show();
    }

//    private void takeSnapshot(final Scene scene) {
//        // Take snapshot of the scene
//        final WritableImage writableImage = scene.snapshot(null);
//
//        // Write snapshot to file system as a .png image
//        final File outFile = new File("snapshot/radialmenu-snapshot-"
//                + snapshotCounter + ".png");
//        outFile.getParentFile().mkdirs();
//        try {
//            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png",
//                    outFile);
//        } catch (final IOException ex) {
//            System.out.println(ex.getMessage());
//        }
//
//        snapshotCounter++;
//    }
}

//    private final ObjectProperty<Color> sceneColorProperty
//            = new SimpleObjectProperty<>(Color.WHITE);
//    @Override
//    public void start(Stage primaryStage) {
//
//        Rectangle rect = new Rectangle(400, 400);
//        rect.fillProperty().bind(sceneColorProperty);
//
//        StackPane pane = new StackPane(rect);
//        
//        Scene scene = new Scene(pane, 400, 400);
//        scene.getStylesheets().add("css/color.css");
//        scene.setOnMouseClicked(e -> {
//            if (e.getButton().equals(MouseButton.SECONDARY)) {
//                CustomColorBox myCustomColorPicker = new CustomColorBox();
//                myCustomColorPicker.setCurrentColor(sceneColorProperty.get());
//
//                CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
//                itemColor.setHideOnClick(false);
//                sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
//                ContextMenu contextMenu = new ContextMenu(itemColor);
//                contextMenu.setOnHiding(t -> sceneColorProperty.unbind());
//                contextMenu.show(scene.getWindow(), e.getScreenX(), e.getScreenY());
//            }
//        });
//
//        primaryStage.setTitle("Custom Color Selector");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//    }

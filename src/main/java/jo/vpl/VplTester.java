package jo.vpl;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.layout.AnchorPane;

import javafx.stage.Stage;
import jo.vpl.core.VplControl;

public class VplTester extends Application {

    int snapshotCounter = 0;

    public void start(Stage stage) throws Exception {
        System.out.println(Thread.currentThread().getName());
        
        AnchorPane pane = new AnchorPane();

        VplControl host = new VplControl();
        Group vplContent = host.Go();

        pane.getChildren().addAll(vplContent);
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

}

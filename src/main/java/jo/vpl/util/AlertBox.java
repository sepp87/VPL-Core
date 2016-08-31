package jo.vpl.util;

import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

/**
 *
 * @author JoostMeulenkamp
 */
public class AlertBox {
    public static void display(String title, String message){
        Stage window = new Stage();
        window.setTitle(title);
        
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);
        
        Label label = new Label();
        label.setText(message);
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());
        
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label,closeButton);
        
        layout.setAlignment(Pos.CENTER);
        
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        
    }
}

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
public class ConfirmBox {

    static boolean answer;
    
    public static boolean display(String title, String message) {
        Stage window = new Stage();
        window.setTitle(title);
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(250);
        Label label = new Label();
        label.setText(message);

        Button confirmButton = new Button("Confirm");
        confirmButton.setOnAction(e -> {
            answer = true;
            window.close();
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            answer = false;
            window.close();
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, cancelButton, confirmButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        return answer;
    }
}

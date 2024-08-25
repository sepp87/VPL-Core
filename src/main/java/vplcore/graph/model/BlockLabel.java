package vplcore.graph.model;

import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockLabel extends Button {

    HBox hostElement;
    TextField field;
    String tempText;

    public BlockLabel(HBox box) {
        hostElement = box;
        field = new TextField();

        HBox.setHgrow(this, Priority.ALWAYS);
        this.setMaxWidth(Double.MAX_VALUE);
        field.maxWidthProperty().bind(this.widthProperty());

        getStyleClass().add("vpl-tag");
        field.getStyleClass().add("vpl-tag");

        field.textProperty().bindBidirectional(textProperty());
        field.setOnKeyPressed(this::field_KeyPress);
        field.setOnMouseExited(this::field_MouseExit);
        setOnMouseClicked(this::handle_MouseClick);

    }

    private void field_KeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            field_MouseExit(e);
        } else if (e.getCode() == KeyCode.ESCAPE) {
            field_MouseExit(e);
            setText(tempText);
        }
    }

    private void field_MouseExit(Event e) {
        if (getText().isEmpty()) {
            setText(tempText);
        }
        int index = hostElement.getChildren().indexOf(field);
        hostElement.getChildren().remove(field);
        hostElement.getChildren().add(index, this);
    }

    private void handle_MouseClick(MouseEvent e) {

        if (e.getButton() != MouseButton.PRIMARY) {
            e.consume();
            return;
        }

        // 3 * 21 + 3 * 5
        if (e.getClickCount() == 2) {
            int index = hostElement.getChildren().indexOf(this);

            tempText = getText();
            hostElement.getChildren().remove(this);
            hostElement.getChildren().add(index, field);
            field.requestFocus();
        }
    }
}

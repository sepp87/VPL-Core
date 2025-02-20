package vplcore.graph.block;

import javafx.event.Event;
import javafx.event.EventHandler;
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
public class BaseLabel extends Button {

    HBox hostElement;
    private final TextField textField;
    private String tempText;

    private final EventHandler<KeyEvent> keyPressedHandler = this::handleKeyPressed;
    private final EventHandler<MouseEvent> fieldExitedHandler = this::handleFieldExited;
    private final EventHandler<MouseEvent> blockLabelClickedHandler = this::handleBlockLabelClicked;

    public BaseLabel(HBox box) {
        hostElement = box;
        textField = new TextField();

        HBox.setHgrow(this, Priority.ALWAYS);
        this.setMaxWidth(Double.MAX_VALUE);
        textField.maxWidthProperty().bind(this.widthProperty());

        getStyleClass().add("vpl-tag");
        textField.getStyleClass().add("vpl-tag");

        textField.textProperty().bindBidirectional(textProperty());
        textField.setOnKeyPressed(keyPressedHandler);
        textField.setOnMouseExited(fieldExitedHandler);
        setOnMouseClicked(blockLabelClickedHandler);
    }

    private void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            handleFieldExited(null);
        } else if (e.getCode() == KeyCode.ESCAPE) {
            handleFieldExited(null);
            setText(tempText);
        }
    }

    private void handleFieldExited(MouseEvent event) {
        if (getText().isEmpty()) {
            setText(tempText);
        }
        int index = hostElement.getChildren().indexOf(textField);
        hostElement.getChildren().remove(textField);
        hostElement.getChildren().add(index, this);
    }

    private void handleBlockLabelClicked(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY) {
            event.consume();
            return;
        }

        // 3 * 21 + 3 * 5
        if (event.getClickCount() == 2) {
            int index = hostElement.getChildren().indexOf(this);

            tempText = getText();
            hostElement.getChildren().remove(this);
            hostElement.getChildren().add(index, textField);
            textField.requestFocus();
        }
    }

    public void delete() {
        textField.maxWidthProperty().unbind();
        textField.textProperty().unbindBidirectional(textProperty());
        textField.setOnKeyPressed(null);
        textField.setOnMouseExited(null);
        setOnMouseClicked(null);
    }
}

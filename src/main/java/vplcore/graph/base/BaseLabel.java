package vplcore.graph.base;

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
import javafx.scene.layout.Region;

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

        HBox.setHgrow(this, Priority.SOMETIMES);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setMinWidth(Region.USE_COMPUTED_SIZE); // Let the parent decide
        this.setPrefWidth(Region.USE_COMPUTED_SIZE); // Let the parent decide
//        this.setPrefWidth(0); // Let the parent decide

//        textField.prefWidthProperty().bind(this.widthProperty());
//        textField.setMinWidth(0); // Allow shrinking
        HBox.setHgrow(textField, Priority.SOMETIMES);
        textField.setMinWidth(Region.USE_COMPUTED_SIZE); // Let the parent decide
        textField.setPrefWidth(Region.USE_COMPUTED_SIZE); // Let the parent decide
        textField.setMaxWidth(Double.MAX_VALUE);

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

    public void remove() {
//        textField.maxWidthProperty().unbind();
        textField.prefWidthProperty().unbind();
        textField.textProperty().unbindBidirectional(textProperty());
        textField.setOnKeyPressed(null);
        textField.setOnMouseExited(null);
        setOnMouseClicked(null);
    }
}

package vpllib.util;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author JoostMeulenkamp
 */
public class TickBox extends HBox {

    VBox ticker;
    Button min;
    Button plus;

    DoubleProperty value;
    StringBinding valueString;
    Button valueButton;
    TextField valueField;

    int step = 1;

    public TickBox() {
        getStylesheets().add("css/ticker.css");

        setPrefHeight(24);
        setMinHeight(24);
        setMaxHeight(24);
        setPrefWidth(176);
        setMinWidth(176);
        setMaxWidth(176);
        setOnMouseEntered(this::tickBox_MouseEnter);
        setOnMouseExited(this::tickBox_MouseExit);

        ticker = new VBox();
        ticker.setVisible(false);

        min = new Button('\uf0d7' + "");
        plus = new Button('\uf0d8' + "");
        min.getStyleClass().add("ticker");
        plus.getStyleClass().add("ticker");
        min.setPrefWidth(26);
        plus.setPrefWidth(26);
        min.prefHeightProperty().bind(Bindings.divide(ticker.heightProperty(), 2));
        plus.prefHeightProperty().bind(Bindings.divide(ticker.heightProperty(), 2));
        min.minHeightProperty().bind(Bindings.divide(ticker.heightProperty(), 2));
        plus.minHeightProperty().bind(Bindings.divide(ticker.heightProperty(), 2));
        min.maxHeightProperty().bind(Bindings.divide(ticker.heightProperty(), 2));
        plus.maxHeightProperty().bind(Bindings.divide(ticker.heightProperty(), 2));

        min.setOnMouseClicked(this::subtractFromValue);
        plus.setOnMouseClicked(this::addToValue);
        min.setOnMouseEntered(this::handle_MouseEnter);
        min.setOnMouseExited(this::handle_MouseExit);
        plus.setOnMouseEntered(this::handle_MouseEnter);
        plus.setOnMouseExited(this::handle_MouseExit);

        ticker.getChildren().addAll(plus, min);
        ticker.prefHeightProperty().bind(heightProperty());

        value = new SimpleDoubleProperty(0);
        valueButton = new Button();
        valueField = new TextField();
        valueButton.getStyleClass().add("value-button");
        valueField.getStyleClass().add("value-field");
        valueString = value.asString("%s");
        valueButton.textProperty().bind(valueString);
        valueField.textProperty().bind(valueString);
        valueButton.prefHeightProperty().bind(heightProperty());
        valueField.prefHeightProperty().bind(heightProperty());
        valueButton.minHeightProperty().bind(heightProperty());
        valueField.minHeightProperty().bind(heightProperty());
        valueButton.maxHeightProperty().bind(heightProperty());
        valueField.maxHeightProperty().bind(heightProperty());
        valueButton.prefWidthProperty().bind(Bindings.subtract(widthProperty(), 24));
        valueField.prefWidthProperty().bind(Bindings.subtract(widthProperty(), 24));
        valueButton.minWidthProperty().bind(Bindings.subtract(widthProperty(), 24));
        valueField.minWidthProperty().bind(Bindings.subtract(widthProperty(), 24));
        valueButton.maxWidthProperty().bind(Bindings.subtract(widthProperty(), 24));
        valueField.maxWidthProperty().bind(Bindings.subtract(widthProperty(), 24));

        valueField.setOnKeyPressed(this::valueField_KeyPress);
        valueField.setOnMouseExited(this::valueField_MouseExit);
        valueButton.setOnMouseClicked(this::valueButton_MouseClick);

        getChildren().addAll(valueButton, ticker);
    }

    private void tickBox_MouseEnter(MouseEvent e) {
        getChildren().get(1).setVisible(true);
    }

    private void tickBox_MouseExit(MouseEvent e) {
        getChildren().get(1).setVisible(false);
    }

    private void handle_MouseEnter(MouseEvent e) {
        e.getPickResult().getIntersectedNode().requestFocus();
    }

    private void handle_MouseExit(MouseEvent e) {
        requestFocus();
    }

    private void valueField_KeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) {
            valueField_MouseExit(e);
        } else if (e.getCode() == KeyCode.ESCAPE) {
            valueField_MouseExit(e);
        }
    }

    private void valueField_MouseExit(Event e) {
        if (!valueField.getText().isEmpty()) {
            String rawValue = valueField.getText();

            //http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
            String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
            boolean isDouble = rawValue.matches(regExp);
            //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
//            String regExp = "^\\d+$";
//            boolean isInteger = rawValue.matches(regExp);

            if (isDouble) {
                Double newValue = Double.parseDouble(rawValue);
                setValue(newValue);
            }
        }
        valueField.textProperty().bind(valueString);
        requestFocus();
        getChildren().remove(0);
        getChildren().add(0, valueButton);
    }

    private void valueButton_MouseClick(MouseEvent e) {

        if (e.getButton() != MouseButton.PRIMARY) {
            e.consume();
            return;
        }
        // 3 * 21 + 3 * 5
        if (e.getClickCount() == 2) {
            valueField.textProperty().unbind();
            getChildren().remove(0);
            getChildren().add(0, valueField);
            valueField.requestFocus();
        }
    }

    private void subtractFromValue(MouseEvent e) {
        Double newValue = getValue() - step;
        setValue(newValue);
    }

    private void addToValue(MouseEvent e) {
        Double newValue = getValue() + step;
        setValue(newValue);
    }

    public DoubleProperty valueProperty() {
        return value;
    }

    public Double getValue() {
        return value.get();
    }

    public void setValue(Double value) {
        this.value.set(value);
    }
}

package btslib.ui;

import java.util.function.Consumer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 *
 * @author joostmeulenkamp
 */
public class NumberSliderExpander extends TitledPane {

    private final Slider slider;
    private final Property<Number> value;
    private final Property<Number> min;
    private final Property<Number> max;
    private final Property<Number> step;
    private final boolean isIntegerSlider;

    private final TextField valueField;
    private final TextField minField;
    private final TextField maxField;
    private final TextField stepField;

    private final EventHandler<KeyEvent> fieldKeyPressedHandler = this::handleFieldKeyPressed;

    public NumberSliderExpander(Slider slider, boolean isIntegerSlider, Property<Number> value, Property<Number> min, Property<Number> max, Property<Number> step) {
        this.slider = slider;
        this.isIntegerSlider = isIntegerSlider;
        this.value = value;
        this.min = min;
        this.max = max;
        this.step = step;

        GridPane grid = new GridPane();
        grid.setVgap(4);
        grid.getColumnConstraints().add(new ColumnConstraints(50)); // column 1 is 100 wide
        grid.getColumnConstraints().add(new ColumnConstraints(103)); // column 2 is 100 wide

        grid.add(new Label("Value:"), 0, 0);
        grid.add(new Label("Min:"), 0, 1);
        grid.add(new Label("Max:"), 0, 2);
        grid.add(new Label("Step:"), 0, 3);

        valueField = new TextField();
        minField = new TextField();
        maxField = new TextField();
        stepField = new TextField();

        valueField.setId("value");
        minField.setId("min");
        maxField.setId("max");
        stepField.setId("step");

        valueField.setOnKeyPressed(fieldKeyPressedHandler);
        minField.setOnKeyPressed(fieldKeyPressedHandler);
        maxField.setOnKeyPressed(fieldKeyPressedHandler);
        stepField.setOnKeyPressed(fieldKeyPressedHandler);

        valueFieldFocusListener = (b, o, n) -> onFocusChanged(n, valueField, Bindings.convert(value), slider::setValue, false);
        minFieldFocusListener = (b, o, n) -> onFocusChanged(n, minField, Bindings.convert(min), slider::setMin, false);
        maxFieldFocusListener = (b, o, n) -> onFocusChanged(n, maxField, Bindings.convert(max), slider::setMax, false);
        stepFieldFocusListener = (b, o, n) -> onFocusChanged(n, stepField, Bindings.convert(step), slider::setBlockIncrement, true);

        valueField.focusedProperty().addListener(valueFieldFocusListener);
        minField.focusedProperty().addListener(minFieldFocusListener);
        maxField.focusedProperty().addListener(maxFieldFocusListener);
        stepField.focusedProperty().addListener(stepFieldFocusListener);

        valueField.textProperty().bind(Bindings.convert(value));
        minField.textProperty().bind(Bindings.convert(min));
        maxField.textProperty().bind(Bindings.convert(max));
        stepField.textProperty().bind(Bindings.convert(step));

        grid.add(valueField, 1, 0);
        grid.add(minField, 1, 1);
        grid.add(maxField, 1, 2);
        grid.add(stepField, 1, 3);

        grid.setPadding(new Insets(5, 5, 0, 10));

        this.setFocusTraversable(false);
        this.setExpanded(false);
        this.setContent(grid);
    }

    private final ChangeListener<Boolean> valueFieldFocusListener;
    private final ChangeListener<Boolean> minFieldFocusListener;
    private final ChangeListener<Boolean> maxFieldFocusListener;
    private final ChangeListener<Boolean> stepFieldFocusListener;

    private void onFocusChanged(boolean isFocused, TextField field, StringExpression numberAsString, Consumer<Double> sliderSetter, boolean requirePositiveValue) {
        if (isFocused) { // Focus gained
            field.textProperty().unbind();
        } else { // Focus lost
            String text = field.getText();
            if (!text.isEmpty()) {
                Double parsed = checkValue(text);
                if (parsed != null && (!requirePositiveValue || parsed > 0)) {
                    sliderSetter.accept(parsed);
                }
            }
            field.textProperty().bind(numberAsString);
        }
    }

    private void handleFieldKeyPressed(KeyEvent keyEvent) {
        TextField field = (TextField) keyEvent.getSource();
        field.textProperty().unbind();
        if (keyEvent.getCode() == KeyCode.ENTER) {
            slider.requestFocus();
        } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
            slider.requestFocus();
        }
    }

    private Double checkValue(String rawValue) {
        Double newValue = null;
        String regex = null;
        if (isIntegerSlider) {
            // http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
            regex = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
        } else {
            // http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
            regex = "^-?\\d+$";
        }

        boolean isNumber = rawValue.matches(regex);

        if (isNumber) {
            newValue = Double.parseDouble(rawValue);
        }
        return newValue;
    }

    public void remove() {
        valueField.setOnKeyPressed(null);
        minField.setOnKeyPressed(null);
        maxField.setOnKeyPressed(null);
        stepField.setOnKeyPressed(null);

        valueField.focusedProperty().removeListener(valueFieldFocusListener);
        minField.focusedProperty().removeListener(minFieldFocusListener);
        maxField.focusedProperty().removeListener(maxFieldFocusListener);
        stepField.focusedProperty().removeListener(stepFieldFocusListener);

        valueField.textProperty().unbind();
        minField.textProperty().unbind();
        maxField.textProperty().unbind();
        stepField.textProperty().unbind();
    }
}

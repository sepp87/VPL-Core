package jo.vpl.hub.input;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import jo.vpl.core.VplControl;
import jo.vpl.core.Hub;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "In.IntegerSlider",
        category = "Input",
        description = "Number Slider",
        tags = {"input", "slider"})
public class IntegerSlider extends Hub {

    Slider slider;
    IntegerProperty integerValue = new SimpleIntegerProperty();
    IntegerProperty integerMin = new SimpleIntegerProperty();
    IntegerProperty integerMax = new SimpleIntegerProperty();
    IntegerProperty integerStep = new SimpleIntegerProperty();

    public IntegerSlider(VplControl hostCanvas) {
        super(hostCanvas);
        setName("Integer");

        addOutPortToHub("int", Integer.class);

        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);
        outPorts.get(0).dataProperty().bind(integerValue);

        integerValue.bindBidirectional(slider.valueProperty());
        integerMin.bindBidirectional(slider.minProperty());
        integerMax.bindBidirectional(slider.maxProperty());
        integerStep.bindBidirectional(slider.blockIncrementProperty());

        Expander expand = new Expander();

        Pane p = new Pane();
        expand.setLayoutX(0);
        expand.setLayoutY(0);
        slider.setLayoutX(30);
        slider.setLayoutY(4);
        p.getChildren().addAll(expand, slider);

        addControlToHub(p);

        setOnMouseEntered(this::handle_MouseEnter);
    }

    private void handle_MouseEnter(Event e) {
        slider.requestFocus();
    }

    class Expander extends TitledPane {

        TextField valueField;
        TextField minField;
        TextField maxField;
        TextField stepField;

        public Expander() {
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

            valueField.setOnKeyPressed(this::field_KeyPress);
            minField.setOnKeyPressed(this::field_KeyPress);
            maxField.setOnKeyPressed(this::field_KeyPress);
            stepField.setOnKeyPressed(this::field_KeyPress);

            valueField.focusedProperty().addListener(this::valueField_FocusChange);
            minField.focusedProperty().addListener(this::minField_FocusChange);
            maxField.focusedProperty().addListener(this::maxField_FocusChange);
            stepField.focusedProperty().addListener(this::stepField_FocusChange);

            valueField.textProperty().bind(integerValueProperty().asString());
            minField.textProperty().bind(integerMinProperty().asString());
            maxField.textProperty().bind(integerMaxProperty().asString());
            stepField.textProperty().bind(integerStepProperty().asString());

            grid.add(valueField, 1, 0);
            grid.add(minField, 1, 1);
            grid.add(maxField, 1, 2);
            grid.add(stepField, 1, 3);

            grid.setPadding(new Insets(5, 5, 0, 10));

            this.setFocusTraversable(false);
            this.setExpanded(false);
            this.setContent(grid);
        }

        private void valueField_FocusChange(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                valueField.textProperty().unbind();
            } else {
                if (!valueField.getText().isEmpty()) {
                    Integer newValue = checkValue(valueField.getText());
                    if (newValue != null) {
                        slider.setValue(newValue);
                    }
                }
                valueField.textProperty().bind(integerValueProperty().asString());
            }
        }

        private void minField_FocusChange(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                minField.textProperty().unbind();
            } else {
                if (!minField.getText().isEmpty()) {
                    Integer newValue = checkValue(minField.getText());
                    if (newValue != null) {
                        slider.setMin(newValue);
                    }
                }
                minField.textProperty().bind(integerMinProperty().asString());
            }
        }

        private void maxField_FocusChange(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                maxField.textProperty().unbind();
            } else {
                if (!maxField.getText().isEmpty()) {
                    Integer newValue = checkValue(maxField.getText());
                    if (newValue != null) {
                        slider.setMax(newValue);
                    }
                }
                maxField.textProperty().bind(integerMaxProperty().asString());
            }
        }

        private void stepField_FocusChange(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                stepField.textProperty().unbind();
            } else {
                if (!stepField.getText().isEmpty()) {
                    Integer newValue = checkValue(stepField.getText());

                    if (newValue != null && !(newValue <= 0)) {
                        slider.setBlockIncrement(newValue);
                    }
                }
                stepField.textProperty().bind(integerStepProperty().asString());
            }
        }

        private void field_KeyPress(KeyEvent e) {
            TextField field = (TextField) e.getSource();
            field.textProperty().unbind();
            if (e.getCode() == KeyCode.ENTER) {
                slider.requestFocus();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                slider.requestFocus();
            }
        }

        private Integer checkValue(String rawValue) {
            Integer newValue = null;
            //http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
//            String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
//            boolean isDouble = rawValue.matches(regExp);
            //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
            String regExp = "^\\d+$";
            boolean isInteger = rawValue.matches(regExp);

            if (isInteger) {
                newValue = Integer.parseInt(rawValue);
            }
            return newValue;
        }
    }

    @Override
    public void calculate() {

    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), integerValueProperty().get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("min"), integerMinProperty().get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("max"), integerMaxProperty().get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("step"), integerStepProperty().get() + "");
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        Integer value = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("value")));
        Integer min = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("min")));
        Integer max = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("max")));
        Integer step = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("step")));
        this.slider.setValue(value);
        this.slider.setMin(min);
        this.slider.setMax(max);
        this.slider.setBlockIncrement(step);
    }

    @Override
    public Hub clone() {
        IntegerSlider hub = new IntegerSlider(hostCanvas);
        hub.slider.setValue(this.slider.getValue());
        hub.slider.setMin(this.slider.getMin());
        hub.slider.setMax(this.slider.getMax());
        hub.slider.setBlockIncrement(this.slider.getBlockIncrement());
        return hub;
    }

    IntegerProperty integerValueProperty() {
        return integerValue;
    }

    IntegerProperty integerMinProperty() {
        return integerMin;
    }

    IntegerProperty integerMaxProperty() {
        return integerMax;
    }

    IntegerProperty integerStepProperty() {
        return integerStep;
    }
}

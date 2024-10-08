package vpllib.input;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import vplcore.workspace.Workspace;
import vplcore.graph.model.Block;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javax.xml.namespace.QName;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockInfo(identifier = "Input.doubleSlider",
        category = "Input",
        description = "Number Slider",
        tags = {"input", "slider"})
public class DoubleSlider extends Block {

    private final Slider slider;
    private final DoubleBinding doubleValue;

    private final EventHandler<MouseEvent> blockEnteredHandler = this::handleMouseEnter;

    public DoubleSlider(Workspace hostCanvas) {
        super(hostCanvas);
        setName("Double");

        addOutPortToBlock("double", Double.class);

        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(0.1);
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);

        DoubleProperty sliderStep = slider.blockIncrementProperty();
        doubleValue = new DoubleBinding() {

            {
                super.bind(sliderStep, slider.valueProperty());
            }

            @Override
            protected double computeValue() {
                int places = getNumberOfDecimalPlaces(sliderStep.get());
                Double bd = new BigDecimal(slider.valueProperty().get()).setScale(places, RoundingMode.HALF_UP).doubleValue();
                return bd;
            }
        };
        outPorts.get(0).dataProperty().bind(doubleValue);

        Expander expand = new Expander();

        Pane p = new Pane();
        expand.setLayoutX(0);
        expand.setLayoutY(0);
        slider.setLayoutX(30);
        slider.setLayoutY(4);
        p.getChildren().addAll(expand, slider);

        addControlToBlock(p);

        setOnMouseEntered(blockEnteredHandler);
    }

    private int getNumberOfDecimalPlaces(Double value) {
        String str = value + "";
        return str.length() - str.indexOf('.') - 1;
    }

    private void handleMouseEnter(Event e) {
        slider.requestFocus();
    }

    class Expander extends TitledPane {

        private final TextField valueField;
        private final TextField minField;
        private final TextField maxField;
        private final TextField stepField;

        private final EventHandler<KeyEvent> fieldKeyPressedHandler = this::handleFieldKeyPressed;
        private final ChangeListener<Object> valueFieldFocusChangedHandler = this::handleValueFieldFocusChanged;
        private final ChangeListener<Object> minFieldFocusedChangedHandler = this::handleMinFieldFocusChanged;
        private final ChangeListener<Object> maxFieldFocusedChangedHandler = this::handleMaxFieldFocusChanged;
        private final ChangeListener<Object> stepFieldFocusChangedHandler = this::handleStepFieldFocusChanged;

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

            valueField.setOnKeyPressed(fieldKeyPressedHandler);
            minField.setOnKeyPressed(fieldKeyPressedHandler);
            maxField.setOnKeyPressed(fieldKeyPressedHandler);
            stepField.setOnKeyPressed(fieldKeyPressedHandler);

            valueField.focusedProperty().addListener(valueFieldFocusChangedHandler);
            minField.focusedProperty().addListener(minFieldFocusedChangedHandler);
            maxField.focusedProperty().addListener(maxFieldFocusedChangedHandler);
            stepField.focusedProperty().addListener(stepFieldFocusChangedHandler);

            valueField.textProperty().bind(doubleValue.asString());
            minField.textProperty().bind(slider.minProperty().asString());
            maxField.textProperty().bind(slider.maxProperty().asString());
            stepField.textProperty().bind(slider.blockIncrementProperty().asString());

            grid.add(valueField, 1, 0);
            grid.add(minField, 1, 1);
            grid.add(maxField, 1, 2);
            grid.add(stepField, 1, 3);

            grid.setPadding(new Insets(5, 5, 0, 10));

            this.setFocusTraversable(false);
            this.setExpanded(false);
            this.setContent(grid);
        }

        private void handleValueFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                valueField.textProperty().unbind();
            } else {
                if (!valueField.getText().isEmpty()) {
                    Double newValue = checkValue(valueField.getText());
                    if (newValue != null) {
                        slider.setValue(newValue);
                    }
                }
                valueField.textProperty().bind(doubleValue.asString());
            }
        }

        private void handleMinFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                minField.textProperty().unbind();
            } else {
                if (!minField.getText().isEmpty()) {
                    Double newValue = checkValue(minField.getText());
                    if (newValue != null) {
                        slider.setMin(newValue);
                    }
                }
                minField.textProperty().bind(slider.minProperty().asString());
            }
        }

        private void handleMaxFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                maxField.textProperty().unbind();
            } else {
                if (!maxField.getText().isEmpty()) {
                    Double newValue = checkValue(maxField.getText());
                    if (newValue != null) {
                        slider.setMax(newValue);
                    }
                }
                maxField.textProperty().bind(slider.maxProperty().asString());
            }
        }

        private void handleStepFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
            boolean focused = (boolean) obj.getValue();
            if (focused) {
                stepField.textProperty().unbind();
            } else {
                if (!stepField.getText().isEmpty()) {
                    Double newValue = checkValue(stepField.getText());

                    if (newValue != null && !(newValue <= 0)) {
                        slider.setBlockIncrement(newValue);
                    }
                }
                stepField.textProperty().bind(slider.blockIncrementProperty().asString());
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
            //http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
            String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
            boolean isDouble = rawValue.matches(regExp);
            //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
//            String regExp = "^\\d+$";
//            boolean isInteger = rawValue.matches(regExp);

            if (isDouble) {
                newValue = Double.parseDouble(rawValue);
            }
            return newValue;
        }
    }

    @Override
    public void calculate() {

    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), slider.getValue() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("min"), slider.getMin() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("max"), slider.getMax() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("step"), slider.getBlockIncrement() + "");
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        Double value = Double.parseDouble(xmlTag.getOtherAttributes().get(QName.valueOf("value")));
        Double min = Double.parseDouble(xmlTag.getOtherAttributes().get(QName.valueOf("min")));
        Double max = Double.parseDouble(xmlTag.getOtherAttributes().get(QName.valueOf("max")));
        Double step = Double.parseDouble(xmlTag.getOtherAttributes().get(QName.valueOf("step")));
        this.slider.setValue(value);
        this.slider.setMin(min);
        this.slider.setMax(max);
        this.slider.setBlockIncrement(step);
    }

    @Override
    public Block clone() {
        DoubleSlider block = new DoubleSlider(workspace);
        block.slider.setValue(this.slider.getValue());
        block.slider.setMin(this.slider.getMin());
        block.slider.setMax(this.slider.getMax());
        block.slider.setBlockIncrement(this.slider.getBlockIncrement());
        return block;
    }
}

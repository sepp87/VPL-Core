package vpllib.input;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javax.xml.namespace.QName;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.integerSlider",
        category = "Input",
        description = "Number Slider",
        tags = {"input", "slider"})
public class IntegerSlider extends BlockModel {

    private Slider slider;
    private Expander expander;

    private final IntegerProperty integerValue = new SimpleIntegerProperty(0);
    private final IntegerProperty integerMin = new SimpleIntegerProperty(0);
    private final IntegerProperty integerMax = new SimpleIntegerProperty(10);
    private final IntegerProperty integerStep = new SimpleIntegerProperty(1);

    public IntegerSlider(WorkspaceModel workspaceModel) {
        super(workspaceModel);
        this.nameProperty().set("Integer");

        addOutputPort("int", Integer.class);
    }

    @Override
    public Region getCustomization() {
        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);
        outputPorts.get(0).dataProperty().bind(integerValue);

        slider.valueProperty().bindBidirectional(integerValue);
        slider.minProperty().bindBidirectional(integerMin);
        slider.maxProperty().bindBidirectional(integerMax);
        slider.blockIncrementProperty().bindBidirectional(integerStep);

        Pane container = new Pane();
        Expander expander = new Expander();
        expander.setLayoutX(0);
        expander.setLayoutY(0);
        slider.setLayoutX(30);
        slider.setLayoutY(4);
        container.getChildren().addAll(expander, slider);
        return container;
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return this::focusOnSlider;
    }

    public void focusOnSlider(MouseEvent event) {
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

        private void handleValueFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
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

        private void handleMinFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
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

        private void handleMaxFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
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

        private void handleStepFieldFocusChanged(ObservableValue obj, Object oldVal, Object newVal) {
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

        private void handleFieldKeyPressed(KeyEvent keyEvent) {
            TextField field = (TextField) keyEvent.getSource();
            field.textProperty().unbind();
            if (keyEvent.getCode() == KeyCode.ENTER) {
                slider.requestFocus();
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                slider.requestFocus();
            }
        }

        private Integer checkValue(String rawValue) {
            Integer newValue = null;
            //http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
//            String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
//            boolean isDouble = rawValue.matches(regExp);
            //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
            String regExp = "^-?\\d+$";
            boolean isInteger = rawValue.matches(regExp);

            if (isInteger) {
                newValue = Integer.parseInt(rawValue);
            }
            return newValue;
        }

        private void remove() {
            valueField.setOnKeyPressed(null);
            minField.setOnKeyPressed(null);
            maxField.setOnKeyPressed(null);
            stepField.setOnKeyPressed(null);

            valueField.focusedProperty().removeListener(valueFieldFocusChangedHandler);
            minField.focusedProperty().removeListener(minFieldFocusedChangedHandler);
            maxField.focusedProperty().removeListener(maxFieldFocusedChangedHandler);
            stepField.focusedProperty().removeListener(stepFieldFocusChangedHandler);

            valueField.textProperty().unbind();
            minField.textProperty().unbind();
            maxField.textProperty().unbind();
            stepField.textProperty().unbind();
        }
    }

    @Override
    public void process() {

    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), integerValueProperty().get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("min"), integerMinProperty().get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("max"), integerMaxProperty().get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("step"), integerStepProperty().get() + "");
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        Integer value = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("value")));
        Integer min = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("min")));
        Integer max = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("max")));
        Integer step = Integer.parseInt(xmlTag.getOtherAttributes().get(QName.valueOf("step")));
        this.integerValue.set(value);
        this.integerMin.set(min);
        this.integerMax.set(max);
        this.integerStep.set(step);
    }

    @Override
    public BlockModel copy() {
        IntegerSlider block = new IntegerSlider(workspace);
        block.integerValue.set(this.integerValue.get());
        block.integerMin.set(this.integerMin.get());
        block.integerMax.set(this.integerMax.get());
        block.integerStep.set(this.integerStep.get());
        return block;
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

    @Override
    public void remove() {
        super.remove();
        outputPorts.get(0).dataProperty().unbind();
        if (slider != null) {
            slider.valueProperty().unbindBidirectional(integerValue);
            slider.minProperty().unbindBidirectional(integerMin);
            slider.maxProperty().unbindBidirectional(integerMax);
            slider.blockIncrementProperty().unbindBidirectional(integerStep);
        }

        if (expander != null) {
            expander.remove();
        }
    }
}

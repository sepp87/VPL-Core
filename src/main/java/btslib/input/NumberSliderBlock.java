package btslib.input;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockModel;
import btscore.graph.port.PortModel;
import btslib.ui.Expander;
import javafx.beans.property.Property;

/**
 *
 * @author JoostMeulenkamp
 */
public abstract class NumberSliderBlock extends BlockModel {

    protected final PortModel output;

    protected Property<Number> value;
    protected Property<Number> min;
    protected Property<Number> max;
    protected Property<Number> step;

    private Slider slider;
    private Expander expander;

    public NumberSliderBlock() {
        this.nameProperty().set("Number");
        output = addOutputPort("value", Number.class);
        initializeProperties();
        initialize();
    }

    protected abstract void initializeProperties();

    @Override
    protected final void initialize() {
        outputPorts.get(0).dataProperty().bind(value);
    }

    @Override
    public Region getCustomization() {
        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(step.getValue().doubleValue());
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);

        slider.valueProperty().bindBidirectional(value);
        slider.minProperty().bindBidirectional(min);
        slider.maxProperty().bindBidirectional(max);
        slider.blockIncrementProperty().bindBidirectional(step);

        Pane container = new Pane();
        expander = new Expander(slider, false, value, min, max, step);
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

    @Override
    public void process() {

    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), value.getValue().toString());
        xmlTag.getOtherAttributes().put(QName.valueOf("min"), min.getValue().toString());
        xmlTag.getOtherAttributes().put(QName.valueOf("max"), max.getValue().toString());
        xmlTag.getOtherAttributes().put(QName.valueOf("step"), step.getValue().toString());
    }

    @Override
    public void onRemoved() {
        outputPorts.get(0).dataProperty().unbind();
        if (slider != null) {
            slider.valueProperty().unbindBidirectional(value);
            slider.minProperty().unbindBidirectional(min);
            slider.maxProperty().unbindBidirectional(max);
            slider.blockIncrementProperty().unbindBidirectional(step);
        }

        if (expander != null) {
            expander.remove();
        }
    }

}

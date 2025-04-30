package btslib.input;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.doubleSlider",
        category = "Input",
        description = "Number Slider",
        tags = {"input", "slider"})
public class DoubleSlider extends BlockModel {
    
    private Slider slider;
    private btslib.ui.Expander expander;
    
    private final DoubleProperty doubleValue = new SimpleDoubleProperty(0);
    private final DoubleProperty doubleMin = new SimpleDoubleProperty(0);
    private final DoubleProperty doubleMax = new SimpleDoubleProperty(10);
    private final DoubleProperty doubleStep = new SimpleDoubleProperty(0.1);
    
    public DoubleSlider() {
        this.nameProperty().set("Double");
        addOutputPort("double", Double.class);
        initialize();
    }
    
    @Override
    protected final void initialize() {
        outputPorts.get(0).dataProperty().bind(doubleValue);
    }
    
    @Override
    public Region getCustomization() {
        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(0.1);
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);
        
        slider.valueProperty().bindBidirectional(doubleValue);
        slider.minProperty().bindBidirectional(doubleMin);
        slider.maxProperty().bindBidirectional(doubleMax);
        slider.blockIncrementProperty().bindBidirectional(doubleStep);
        
        Pane container = new Pane();
        expander = new btslib.ui.Expander(slider, false, doubleValue, doubleMin, doubleMax, doubleStep);
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
        this.doubleValue.set(value);
        this.doubleMin.set(min);
        this.doubleMax.set(max);
        this.doubleStep.set(step);
    }
    
    @Override
    public BlockModel copy() {
        DoubleSlider block = new DoubleSlider();
        block.doubleValue.set(this.doubleValue.get());
        block.doubleMin.set(this.doubleMin.get());
        block.doubleMax.set(this.doubleMax.get());
        block.doubleStep.set(this.doubleStep.get());
        return block;
    }
    
    @Override
    public void onRemoved() {
        outputPorts.get(0).dataProperty().unbind();
        if (slider != null) {
            slider.valueProperty().unbindBidirectional(doubleValue);
            slider.minProperty().unbindBidirectional(doubleMin);
            slider.maxProperty().unbindBidirectional(doubleMax);
            slider.blockIncrementProperty().unbindBidirectional(doubleStep);
        }
        
        if (expander != null) {
            expander.remove();
        }
    }
    
}

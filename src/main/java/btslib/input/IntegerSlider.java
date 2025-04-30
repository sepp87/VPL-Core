package btslib.input;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

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
    private btslib.ui.Expander expander;

    private final IntegerProperty integerValue = new SimpleIntegerProperty(0);
    private final IntegerProperty integerMin = new SimpleIntegerProperty(0);
    private final IntegerProperty integerMax = new SimpleIntegerProperty(10);
    private final IntegerProperty integerStep = new SimpleIntegerProperty(1);

    public IntegerSlider() {
        this.nameProperty().set("Integer");
        addOutputPort("int", Integer.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        outputPorts.get(0).dataProperty().bind(integerValue);
    }

    @Override
    public Region getCustomization() {
        slider = new Slider(0, 10, 0);
        slider.setBlockIncrement(1);
        slider.setSnapToTicks(true);
        slider.majorTickUnitProperty().bind(slider.blockIncrementProperty());
        slider.setMinorTickCount(0);

        slider.valueProperty().bindBidirectional(integerValue);
        slider.minProperty().bindBidirectional(integerMin);
        slider.maxProperty().bindBidirectional(integerMax);
        slider.blockIncrementProperty().bindBidirectional(integerStep);

        Pane container = new Pane();
        expander = new btslib.ui.Expander(slider, false, integerValue, integerMin, integerMax, integerStep);
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
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), integerValue.get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("min"), integerMin.get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("max"), integerMax.get() + "");
        xmlTag.getOtherAttributes().put(QName.valueOf("step"), integerStep.get() + "");
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
        IntegerSlider block = new IntegerSlider();
//        IntegerSlider block = new IntegerSlider(workspace);
        block.integerValue.set(this.integerValue.get());
        block.integerMin.set(this.integerMin.get());
        block.integerMax.set(this.integerMax.get());
        block.integerStep.set(this.integerStep.get());
        return block;
    }

    @Override
    public void onRemoved() {
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

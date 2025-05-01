package btslib.input;

import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.doubleSlider",
        category = "Input",
        description = "Double Slider",
        tags = {"input", "slider"})
public class DoubleSliderBlock extends NumberSliderBlock {

    public DoubleSliderBlock() {
        this.nameProperty().set("Double");
        output.dataTypeProperty().set(Double.class);
    }

    @Override
    protected void initializeProperties() {
        this.value = new SimpleDoubleProperty(0);
        this.min = new SimpleDoubleProperty(0);
        this.max = new SimpleDoubleProperty(10);
        this.step = new SimpleDoubleProperty(0.1);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("value"));
        String min = xmlTag.getOtherAttributes().get(QName.valueOf("min"));
        String max = xmlTag.getOtherAttributes().get(QName.valueOf("max"));
        String step = xmlTag.getOtherAttributes().get(QName.valueOf("step"));
        this.value.setValue(Double.valueOf(value));
        this.min.setValue(Double.valueOf(min));
        this.max.setValue(Double.valueOf(max));
        this.step.setValue(Double.valueOf(step));
    }

    @Override
    public BlockModel copy() {
        DoubleSliderBlock block = new DoubleSliderBlock();
        block.value.setValue(this.value.getValue());
        block.min.setValue(this.min.getValue());
        block.max.setValue(this.max.getValue());
        block.step.setValue(this.step.getValue());
        return block;
    }

}

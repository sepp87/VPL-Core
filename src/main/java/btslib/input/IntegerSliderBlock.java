package btslib.input;

import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.integerSlider",
        category = "Input",
        description = "Integer Slider",
        tags = {"input", "slider"})
public class IntegerSliderBlock extends NumberSliderBlock {

    public IntegerSliderBlock() {
        this.nameProperty().set("Integer");
        output.dataTypeProperty().set(Integer.class);
    }

    @Override
    protected void initializeProperties() {
        this.value = new SimpleIntegerProperty(0);
        this.min = new SimpleIntegerProperty(0);
        this.max = new SimpleIntegerProperty(10);
        this.step = new SimpleIntegerProperty(1);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("value"));
        String min = xmlTag.getOtherAttributes().get(QName.valueOf("min"));
        String max = xmlTag.getOtherAttributes().get(QName.valueOf("max"));
        String step = xmlTag.getOtherAttributes().get(QName.valueOf("step"));
        this.value.setValue(Integer.valueOf(value));
        this.min.setValue(Integer.valueOf(min));
        this.max.setValue(Integer.valueOf(max));
        this.step.setValue(Integer.valueOf(step));
    }

    @Override
    public BlockModel copy() {
        IntegerSliderBlock block = new IntegerSliderBlock();
        block.value.setValue(this.value.getValue());
        block.min.setValue(this.min.getValue());
        block.max.setValue(this.max.getValue());
        block.step.setValue(this.step.getValue());
        return block;
    }

}

package vpllib.input;

import vplcore.graph.model.Block;
import vplcore.workspace.WorkspaceController;
import javafx.scene.paint.Color;
import javax.xml.namespace.QName;
import vpllib.util.CustomColorBox;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockInfo(
        identifier = "Input.color",
        category = "Input",
        description = "Pick a nice color from the palette",
        tags = {"input", "color"})
public class ColorBlock extends Block {

    public ColorBlock(WorkspaceController hostCanvas) {
        super(hostCanvas);

        setName("Color Picker");

        addOutPortToBlock("color", Color.class);

        CustomColorBox picker = new CustomColorBox();

        outPorts.get(0).dataProperty().bind(picker.customColorProperty());

        addControlToBlock(picker);
    }

    public Color getColor() {
        CustomColorBox picker = (CustomColorBox) controls.get(0);
        return picker.customColorProperty().get();
    }

    public void setColor(Color color) {
        CustomColorBox picker = (CustomColorBox) controls.get(0);
        picker.customColorProperty().set(color);
    }

    @Override
    public void calculate() {
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("color"), getColor().toString());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String color = xmlTag.getOtherAttributes().get(QName.valueOf("color"));
        this.setColor(Color.valueOf(color));
    }

    @Override
    public Block clone() {
        ColorBlock block = new ColorBlock(workspaceController);
        block.setColor(this.getColor());
        return block;
    }
}

package vpllib.input;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javax.xml.namespace.QName;
import vpllib.util.CustomColorBox;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockMetadata;
import vplcore.workspace.BlockModel;
import vplcore.workspace.WorkspaceModel;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.color",
        category = "Input",
        description = "Pick a nice color from the palette",
        tags = {"input", "color"})
public class ColorBlockNew extends BlockModel {

    private CustomColorBox picker;

    public ColorBlockNew(WorkspaceModel workspaceModel) {
        super(workspaceModel);
        this.nameProperty().set("Color Picker");
        addOutputPort("color", Color.class);
    }

    @Override
    public Region getCustomization() {
        picker = new CustomColorBox();
        outputPorts.get(0).dataProperty().bind(picker.customColorProperty());
        return picker;
    }

    public Color getColor() {
        return picker.customColorProperty().get();
    }

    public void setColor(Color color) {
        picker.customColorProperty().set(color);
    }

    @Override
    public void process() {
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
    public BlockModel copy() {
        ColorBlockNew block = new ColorBlockNew(workspace);
        block.setColor(this.getColor());
        return block;
    }

}

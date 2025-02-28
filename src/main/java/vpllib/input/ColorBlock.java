package vpllib.input;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javax.xml.namespace.QName;
import vpllib.util.CustomColorBox;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockModel;
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
public class ColorBlock extends BlockModel {

    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);
    private CustomColorBox picker;

    public ColorBlock(WorkspaceModel workspaceModel) {
        super(workspaceModel);
        this.nameProperty().set("Color Picker");
        addOutputPort("color", Color.class);
        outputPorts.get(0).dataProperty().bind(color);
    }

    @Override
    public Region getCustomization() {
        picker = new CustomColorBox();
        picker.customColorProperty().bindBidirectional(color);
        return picker;
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    @Override
    public void process() {
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("color"), color.get().toString());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("color"));
        color.set(Color.valueOf(value));
    }

    @Override
    public BlockModel copy() {
        ColorBlock block = new ColorBlock(workspace);
        block.color.set(this.color.get());
        return block;
    }

    @Override
    public void remove() {
        super.remove();
        outputPorts.get(0).dataProperty().unbind();
        if (picker != null) {
            picker.customColorProperty().unbindBidirectional(color);
        }
    }

}

package btslib.template;

import btscore.icons.FontAwesomeSolid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Util.Template",
        category = "General",
        description = "A template block for further customization",
        tags = {"template", "dummy", "example"})
public class TemplateBlock extends BlockModel {

    public TemplateBlock() {
        this.nameProperty().set("Template");
        addInputPort("Object", Object.class);
        addOutputPort("String", String.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        // Event handlers, change listeners and bindings
    }

    @Override
    public Region getCustomization() {
        Label label = BlockView.getAwesomeIcon(FontAwesomeSolid.PAPER_PLANE);
        return label;
    }

    /**
     * process function is called whenever new data is incoming
     */
    @Override
    public void process() {

        // Get incoming data
        Object raw = inputPorts.get(0).getData();

        // Finish calculate if there is no incoming data
        if (raw == null) {
            outputPorts.get(0).setData(null);
            return;
        }

        // Process incoming data
        if (raw instanceof List) {
            List<Object> nodes = (List<Object>) raw;

            // Example code to handle collections
            List<String> strList = nodes.stream()
                    .map(e -> e.toString())
                    .collect(Collectors.toCollection(ArrayList<String>::new));

            // Set outgoing data
            outputPorts.get(0).setData(strList);

        } else {
            // Example code to handle a single object instance
            String str = ((Object) raw).toString();

            // Set outgoing data
            outputPorts.get(0).setData(str);
        }
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        // Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("key"), "value");
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        // Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("key"));
        // Specify further initialization statements here
        this.processSafely();
    }

    @Override
    public BlockModel copy() {
        TemplateBlock block = new TemplateBlock();
        // Specify further copy statements here
        return block;
    }

    @Override
    protected void onRemoved() {
        // Remove event handlers, change listeners and bindings
    }
}

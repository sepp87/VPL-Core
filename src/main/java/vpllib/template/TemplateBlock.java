package vpllib.template;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import vplcore.IconType;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockMetadata;
import vplcore.workspace.BlockModel;
import vplcore.workspace.BlockView;
import vplcore.workspace.PortModel;
import vplcore.workspace.WorkspaceModel;

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

    public TemplateBlock(WorkspaceModel workspace) {
        super(workspace);
        nameProperty().set("Template");

        addInputPort("Object", Object.class);
        addOutputPort("String", String.class);
    }

    @Override
    public Region getCustomization() {
        Label label = BlockView.getAwesomeIcon(IconType.FA_PAPER_PLANE);
        return label;
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return null;
    }

    /**
     * Function to handle data when a connection is added and before calculate
     * is called
     */
    public void handleIncomingConnectionAdded(PortModel source, PortModel incoming) {
        //Sample code for handling just specific ports
        int index = inputPorts.indexOf(source);
        if (index == 0) {

        }
    }

    /**
     * Function to handle data when a connection is removed
     */
    public void handleIncomingConnectionRemoved(PortModel source) {
        //Sample code for handling just specific ports
        int index = inputPorts.indexOf(source);
        if (index == 0) {

        }
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void process() {

        //Get incoming data
        Object raw = inputPorts.get(0).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outputPorts.get(0).setData(null);
            return;
        }

        //Process incoming data
        if (raw instanceof List) {
            List<Object> nodes = (List<Object>) raw;

            //Example code to handle collections
            List<String> strList = nodes.stream()
                    .map(e -> e.toString())
                    .collect(Collectors.toCollection(ArrayList<String>::new));

            //Set outgoing data
            outputPorts.get(0).setData(strList);

        } else {
            //Example code to handle a single object instance
            String str = ((Object) raw).toString();

            //Set outgoing data
            outputPorts.get(0).setData(str);
        }
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("key"), "value");
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("key"));
        //Specify further initialization statements here
        this.process();
    }

    @Override
    public BlockModel copy() {
        TemplateBlock block = new TemplateBlock(workspace);
        //Specify further copy statements here
        return block;
    }
}

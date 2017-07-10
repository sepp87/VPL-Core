package jo.vpl.hub;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "Util.Template",
        category = "General",
        description = "A template hub for further customization",
        tags = {"template", "dummy", "example"})
public class TemplateHub extends Hub {

    public TemplateHub(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Template");

        addInPortToHub("obj", Object.class);

        addOutPortToHub("str", String.class);

        Label label = getAwesomeIcon(IconType.FA_PAPER_PLANE);
        addControlToHub(label);
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        //Get incoming data
        Object raw = inPorts.get(0).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
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
            outPorts.get(0).setData(strList);

        } else {
            //Example code to handle a single object instance
            String str = ((Object) raw).toString();

            //Set outgoing data
            outPorts.get(0).setData(str);
        }
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        //Retrieval of custom attribute
        xmlTag.getOtherAttributes().put(QName.valueOf("key"), "value");
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("key"));
        //Specify further initialization statements here
        this.calculate();
    }

    @Override
    public Hub clone() {
        TemplateHub hub = new TemplateHub(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

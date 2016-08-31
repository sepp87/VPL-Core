package jo.vpl.hub.list;

import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VPLControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "List.Size",
        category = "List",
        description = "Get the list length",
        tags = {"list", "size", "length"})
public class Size extends Hub {

    public Size(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("l");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("list", Object.class);

        addOutPortToHub("size", int.class);

        Label label = new Label("Size");
        label.getStyleClass().add("hub-text");
        
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
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).name = "obj";
            return;
        }

        //Process incoming data
        if (raw instanceof List) {

            List source = (List) raw;

            //Set outgoing data
            outPorts.get(0).setData(source.size());

            //Set data type corresponding to source
            outPorts.get(0).dataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            outPorts.get(0).name = inPorts.get(0).connectedConnections.get(0).getStartPort().name;
        }
    }

    @Override
    public Hub clone() {
        Size hub = new Size(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

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
        name = "List.IsEmpty",
        category = "List",
        description = "Check if a list is empty",
        tags = {"list", "empty"})
public class IsEmpty extends Hub {

    public IsEmpty(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("i");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("list", Object.class);

        addOutPortToHub("bool", boolean.class);

        Label label = new Label("...?");
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

        //Check if all incoming data is in the correct format
        boolean hasError = false;
        if (!(raw instanceof List)) {
            System.out.println("This is not a list");
            hasError = true;
        }
        if (hasError) {
            return;
        }

        //Process incoming data
        List source = (List) raw;

        //Check if list is empty
        boolean out = source.isEmpty();

        //Set outgoing data
        outPorts.get(0).setData(out);

        //Set data type corresponding to source
        outPorts.get(0).dataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
        outPorts.get(0).name = inPorts.get(0).connectedConnections.get(0).getStartPort().name;
    }

    @Override
    public Hub clone() {
        IsEmpty hub = new IsEmpty(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

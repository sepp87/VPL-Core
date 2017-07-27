package jo.vpl.hub.list;

import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "List.GetFirstItem",
        category = "List",
        description = "Get the first item of a list",
        tags = {"list", "get", "item"})
public class GetFirstItem extends Hub {

    public GetFirstItem(VplControl hostCanvas) {
        super(hostCanvas);

        setName("i");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("List", Object.class);
        addOutPortToHub("Object", Object.class);

        Label label = new Label("1st");
        label.getStyleClass().add("hub-text");

        addControlToHub(label);
    }

    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Set data type corresponding to incoming
            outPorts.get(0).dataType = incoming.dataType;
            outPorts.get(0).setName(incoming.dataType.getSimpleName());
        }
    }

    @Override
    public void handle_IncomingConnectionRemoved(Port source) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Reset data type to initial state
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).setName("Object");
        }
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
            outPorts.get(0).setData(null);
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

        if (source.isEmpty()) {
            return;
        }

        //Get first item
        Object out = source.get(0);

        //Set outgoing data
        outPorts.get(0).setData(out);
    }

    @Override
    public Hub clone() {
        GetFirstItem hub = new GetFirstItem(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

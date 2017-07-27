package jo.vpl.hub.list;

import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "List.ReplaceItemAtIndex",
        category = "List",
        description = "Replace an item of a list by its index",
        tags = {"list", "replace"})
public class ReplaceItemAtIndex extends Hub {

    public ReplaceItemAtIndex(VplControl hostCanvas) {
        super(hostCanvas);

        setName("i");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("List", Object.class);
        addInPortToHub("Object", Object.class);
        addInPortToHub("int : Index", int.class);

        addOutPortToHub("Object", Object.class);

        Label label = getAwesomeIcon(IconType.FA_EXCHANGE);
        addControlToHub(label);
    }

    @Override
    public void handle_IncomingConnectionAdded(Port source, Port incoming) {
        int index = inPorts.indexOf(source);
        if (index == 0) {
            //Set data type corresponding to incoming
            outPorts.get(0).dataType = incoming.dataType;
            outPorts.get(0).setName(incoming.getName());
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
        Object element = inPorts.get(1).getData();
        Object index = inPorts.get(2).getData();

        //Finish calculate if there is no incoming data
        if (raw == null || index == null || element == null) {
            outPorts.get(0).setData(null);
            return;
        }

        //Check if all incoming data is in the correct format
        boolean hasError = false;
        if (!(raw instanceof List)) {
            System.out.println("This is not a list");
            hasError = true;
        }
        if (index instanceof List) {
            System.out.println("Just one item can be replaced at a time");
            hasError = true;
        }
        if (element instanceof List) {
            System.out.println("Just one item can be replaced at a time");
            hasError = true;
        }
        if (inPorts.get(1).connectedConnections.size() > 0
                && inPorts.get(0).connectedConnections.size() > 0) {
            Class listDataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            Class itemDataType = inPorts.get(1).connectedConnections.get(0).getStartPort().dataType;
            if (itemDataType.isAssignableFrom(listDataType)) {
                System.out.println("Element is not of same type as the list's");
                hasError = true;
            }
        }

        if (hasError) {
            return;
        }

        //Replace item at index
        List source = (List) raw;

        List target = new ArrayList();
        target.addAll(source);
        target.set((Integer) index, element);

        //Set outgoing data
        outPorts.get(0).setData(target);

    }

    @Override
    public Hub clone() {
        ReplaceItemAtIndex hub = new ReplaceItemAtIndex(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

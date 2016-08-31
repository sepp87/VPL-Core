package jo.vpl.hub.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jo.vpl.core.Hub;
import jo.vpl.core.VPLControl;
import javafx.scene.control.Label;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "List.RemoveItemAtIndex",
        category = "List",
        description = "Remove an item from a list by its index",
        tags = {"list", "remove", "index", "delete"})
public class RemoveItemAtIndex extends Hub {

    public RemoveItemAtIndex(VPLControl hostCanvas) {
        super(hostCanvas);

        setName("i");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("list", Object.class);
        addInPortToHub("int", int.class);

        addOutPortToHub("obj", Object.class);

        Label label = getAwesomeIcon(IconType.FA_DEDENT);
        addControlToHub(label);
    }

    /**
     * calculate function is called whenever new data is incoming
     */
    @Override
    public void calculate() {

        //Get incoming data
        Object raw = inPorts.get(0).getData();
        Object index = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).dataType = Object.class;
            outPorts.get(0).name = "obj";
            return;
        }

        //Process incoming data
        if (raw instanceof List) {

            List source = (List) raw;

            //Example code to handle collections
            Object out = null;
            if (index == null) {
                out = source;
            } else if (index instanceof List) {
                //Remove indices from list starting with heighest number first
                List<Integer> indeces = (List<Integer>) index;
                List<Integer> sortedIndeces = new ArrayList<>();
                sortedIndeces.addAll(indeces);
                Collections.sort(sortedIndeces);
                Collections.reverse(sortedIndeces);
                int size = sortedIndeces.size();
                for (int i = 0; i < size; i++) {
                    List target = new ArrayList<>();
                    target.addAll(source);
                    target.remove(sortedIndeces.get(i));
                    out = target;
                }
            } else {
                List target = new ArrayList<>();
                target.addAll(source);
                int i = (int) index;
                target.remove(i);
                out = target;
            }

            //Set outgoing data
            outPorts.get(0).setData(out);

            //Set data type corresponding to source
            outPorts.get(0).dataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            outPorts.get(0).name = inPorts.get(0).connectedConnections.get(0).getStartPort().name;
        }
    }

    @Override
    public Hub clone() {
        RemoveItemAtIndex hub = new RemoveItemAtIndex(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}

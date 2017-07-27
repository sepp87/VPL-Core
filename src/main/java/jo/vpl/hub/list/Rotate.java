package jo.vpl.hub.list;

import java.util.ArrayList;
import java.util.Collections;
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
        name = "List.Rotate",
        category = "List",
        description = "Rotate the items of a list by an index",
        tags = {"list", "shift"})
public class Rotate extends Hub {

    public Rotate(VplControl hostCanvas) {
        super(hostCanvas);

        setName("i");

        //There is no checking of list in port make connection boolean statement
        //Might want to fix that!
        addInPortToHub("List", Object.class);
        addInPortToHub("int", int.class);

        addOutPortToHub("Object", Object.class);

        Label label = getAwesomeIcon(IconType.FA_REPEAT);
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
        Object index = inPorts.get(1).getData();

        //Finish calculate if there is no incoming data
        if (raw == null) {
            outPorts.get(0).setData(null);
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
                System.out.println("List can not be shifted by multiple numbers");
            } else {
                List target = new ArrayList();
                target.addAll(source);
                Collections.rotate(target, (Integer) index);
                out = target;
            }

            //Set outgoing data
            outPorts.get(0).setData(out);
        }
    }

    @Override
    public Hub clone() {
        Rotate hub = new Rotate(hostCanvas);
        //Specify further copy statements here
        return hub;
    }
}
